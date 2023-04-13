import {useCallback, useEffect, useMemo, useState} from "react";
import { useParams } from "react-router-dom";
import { PriceDataList } from "../../interfaces/IPriceData";
import Highcharts from "highcharts";
import HighchartsReact from "highcharts-react-official";
import appConfig from "../../../config";
import useStocks from "../../hooks/useStocks/useStocks";
import React from "react";
const API_URL = appConfig.URL;

const Stock = () => {
    const { ticker } = useParams<{ ticker: string }>();
    const [data, setData] = useState<PriceDataList | null>(null);
    const {handleRealtimePrice} = useStocks();

    if (!ticker) {
        return <div>Тикер не указан</div>;
    }
    const handleRealtimePriceMemoized = useCallback(() => handleRealtimePrice(ticker), []);
    const { realtimePrice, close } = handleRealtimePriceMemoized();
    useEffect(() => {
        return () => {
            close();
        };
    }, []);


    useEffect(() => {
        const fetchData = async () => {
            const response = await fetch(
                `${API_URL}/api/securities/list/stock/?ticker=${ticker}`,
                {
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json",
                    },
                }
            ).catch((error) => {
                console.log(error);
            }
            ).finally()
            const rawData = await response.json();
            //проблема с тем что дата приходит в формате DD/MM/YYYY и ее нужно правильно спарсить
            const data: PriceDataList = rawData.map((item: any) => ({
                low: parseFloat(item.low),
                high: parseFloat(item.high),
                close: parseFloat(item.close),
                open: parseFloat(item.open),
                date: Date.UTC(
                    parseInt(item.date.split("/")[2]), // year
                    parseInt(item.date.split("/")[1]) - 1, // month (0-indexed)
                    parseInt(item.date.split("/")[0]) // day
                ),
            }));
            setData(data);
            console.log(data);
        };
        fetchData();
    }, [ticker]);

    //PROMISE


    if (!data) {
        return <div>Loading...</div>;
    }

    // @ts-ignore
    const options: Highcharts.Options = {
        title: {
            text: `${ticker} Price Chart`,
            style: {
                color: 'white'
            }
        },
        chart: {
            backgroundColor: 'rgba(255,255,255,0)',
            colorCount: '#FFFFFF',
            width: 700,
        },
        xAxis: {
            type: "datetime",
            dateTimeLabelFormats: {
                month: "%b"
            },
            zoomEnabled: true,
            scrollbar: true,
        },
        yAxis: {
            title: {
                text: "Цена",
            },
        },
        tooltip: {
            formatter: function () {
                // @ts-ignore
                return `<b>${Highcharts.dateFormat('%b %e, %Y', this.x as number)}</b><br/>Цена: ${this.y.toFixed(2)}`;
            }
        },
        rangeSelector: {
            selected: 1
        },
        plotOptions: {
            line: {
                cursor: "pointer",
            },
        },
        tooltip: {
            crosshairs: {
                width: 1,
                color: "gray",
                dashStyle: "solid",
            },
            formatter: function () {
                const date = Highcharts.dateFormat("%d %b %Y", this.x);
                const price = Highcharts.numberFormat(this.y, 2);
                return `${date}<br/><b>${price}</b>`;
            },
        },
        series: [
            {
                name: "Цена",
                lineWidth: 1,
                linecap: 'round',
                color: "#303cc2",
                data: data.map((item) => [Number(new Date(item.date)), item.close]), // переводим даты в миллисекунды
            },
        ],
    };

    return (
        <div>
            <h1>{ticker}</h1>
            <p>Цена: {realtimePrice}</p>
            <HighchartsReact highcharts={Highcharts} options={options} />
        </div>
    );
};

export default Stock;

