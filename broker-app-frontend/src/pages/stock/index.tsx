import { useEffect, useState, useMemo } from "react";
import { useParams, useNavigate } from "react-router-dom";
import useStocks from "../../hooks/useStocks/useStocks";
import { IPriceDataList, IPriceHistoryRequest } from "../../hooks/useStocks/useStocks.model";
import { PriceChart } from "../../components/priceChart/PriceChart";
import {Button, InputNumber, message, Space} from "antd";
import useStockTransactions from "../../hooks/useStockTransactions/useStockTransactions";
import {IStockTransaction} from "../../hooks/useStockTransactions/useStockTransactions.model";
const Stock = () => {
    const navigate = useNavigate();
    const { ticker } = useParams<{ ticker: string }>();
    const [priceHistory, setPriceHistory] = useState<IPriceDataList | null>(
        null
    );

    const [displayMessage, setDisplayMessage] = useState<boolean>(false);
    const { handleRealtimePrice, handlePriceHistory, isLoading } = useStocks();
    const [realtimePrice, setRealtimePrice] = useState<number>(0);

    if (!ticker) {
        navigate("/");
        return null;
    }

    useEffect(() => {
        const priceHistoryRequest: IPriceHistoryRequest = {
            ticker: ticker,
            from: new Date(2021, 0, 1),
            to: new Date(),
            interval: "1d",
        };

        handlePriceHistory(priceHistoryRequest).then(setPriceHistory);
    }, [ticker]);

    useEffect(() => {
        const timer = setTimeout(() => {
            setDisplayMessage(true);
        }, 1500);

        return () => {
            clearTimeout(timer);
        };
    }, []);

    const memoizedRealtimePrice = useMemo(() => realtimePrice, [realtimePrice]);

    useEffect(() => {
        let close: () => void;

        if (ticker) {
            close = handleRealtimePrice(ticker, setRealtimePrice);
        }

        return () => {
            if (close) {
                console.log("WS CLOSED");
                close();
            }
        };
    }, [ticker, handleRealtimePrice]);

    if (!displayMessage && (!memoizedRealtimePrice || !priceHistory)) {
        return <p>загрузка...</p>
    }

    if (memoizedRealtimePrice === 0 && displayMessage) {
        return <>
            <h2>Торги акцией не идут! 💰💰💰</h2>
            <code>ну или какая-то ошибка... Нет информации о ее цене (не пришла по вебсокету)</code></>
    }

    return (
        <div>
            <h1>{ticker}</h1>
            <p>Цена: {memoizedRealtimePrice}</p>
            <PriceChart data={priceHistory} />
            <Controls ticker={ticker} realtimePrice={memoizedRealtimePrice} lot={priceHistory.lot || 1}/>
        </div>
    );
};



    interface IControls { ticker: string, realtimePrice: number, lot: number }
const Controls = ({ ticker, realtimePrice, lot }: IControls) => {
    const [shareQuantity, setShareQuantity] = useState<number>(1);
    const { handleBuyStock, handleSellStock } = useStockTransactions();
    // const { handleRealtimePrice, handlePriceHistory } = useStocks();
    //
    // const memoizedRealtimePrice = useMemo(() => realtimePrice, [realtimePrice]);
    const butStocks = (props: IStockTransaction) => {
        message.success('Куплено!');
        setShareQuantity(0);
        handleBuyStock(props);
    }

    const sellStocks = (props: IStockTransaction) => {
        message.success('Продано!');
        setShareQuantity(0);
        handleSellStock(props);
    }

    return (
        <Space>
            <InputNumber
                min={1}
                value={shareQuantity}
                onChange={(value) => setShareQuantity(value as number)}
                style={{ width: 80 }}
            />
            <p>1 лот = {lot} акций</p>
            <p>Сумма: {realtimePrice * shareQuantity * lot}</p>

            <Button
                onClick={() =>
                    butStocks({ ticker: ticker, value: shareQuantity } as IStockTransaction)
                }
                type="primary"
            >
                Купить
            </Button>
            <Button
                onClick={() =>
                    sellStocks({ ticker: ticker, value: shareQuantity } as IStockTransaction)
                }
                type="primary"
            >
                Продать
            </Button>
        </Space>
    );
};



export default Stock;