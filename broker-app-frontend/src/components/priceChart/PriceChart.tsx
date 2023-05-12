import { CartesianGrid, Line, LineChart, Tooltip, XAxis, YAxis } from "recharts";
import { ICandle, IPriceDataList } from "../../hooks/useStocks/useStocks.model";

interface Props {
    data: IPriceDataList;
}

export const PriceChart: React.FC<Props> = ({ data }) => {
    const chartData = data.candles.map((candle: ICandle) => ({
        date: candle.date,
        close: candle.close,
    }));

    const CustomTooltip = (props: any) => {
        if (props.active && props.payload) {
            const { date, close } = props.payload[0].payload;

            return (
                <div className="custom-tooltip">
                    <p>Дата: {date}</p>
                    <p>Цена закрытия: {close}</p>
                </div>
            );
        }
        return null;
    };

    return (
        <LineChart
            width={800}
            height={400}
            data={chartData}
            margin={{ top: 10, right: 30, left: 0, bottom: 0 }}
        >
            <XAxis dataKey="date" />
            <YAxis type="number" domain={["dataMin", "dataMax"]} />
            <CartesianGrid strokeDasharray="3 3" />
            <Tooltip content={<CustomTooltip />} />
            <Line type="monotone" dataKey="close" stroke="blue" />
        </LineChart>
    );
};