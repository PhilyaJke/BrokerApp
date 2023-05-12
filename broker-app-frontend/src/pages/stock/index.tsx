import { useEffect, useState, useMemo } from "react";
import { useParams, useNavigate } from "react-router-dom";
import useStocks from "../../hooks/useStocks/useStocks";
import { IPriceDataList, IPriceHistoryRequest } from "../../hooks/useStocks/useStocks.model";
import { PriceChart } from "../../components/priceChart/PriceChart";
import { Button, InputNumber, Space } from "antd";
import useStockTransactions from "../../hooks/useStockTransactions/useStockTransactions";
import {IStockTransaction} from "../../hooks/useStockTransactions/useStockTransactions.model";
const Stock = () => {
    const navigate = useNavigate();
    const { handleBuyStock, handleSellStock } = useStockTransactions();
    const { ticker } = useParams<{ ticker: string }>();
    const [shareQuantity, setShareQuantity] = useState<number>(1);
    const [priceHistory, setPriceHistory] = useState<IPriceDataList | null>(
        null
    );
    const { handleRealtimePrice, handlePriceHistory } = useStocks();
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

    if (!priceHistory) {
        return <div>Loading...</div>;
    }


    const handleBuy = () => {
        throw new Error("Function not implemented.");
    }

    const handleSell = () => {
        throw new Error("Function not implemented.");
    }
    return (
        <div>
            <h1>{ticker}</h1>
            <p>Цена: {memoizedRealtimePrice}</p>
            <PriceChart data={priceHistory} />
            <Space>
                <InputNumber
                    min={1}
                    value={shareQuantity}
                    onChange={(value) => setShareQuantity(value as number)}
                    style={{ width: 80 }}
                />
                <p>1 лот = {priceHistory.lot} акций</p>
                <p>Сумма: {memoizedRealtimePrice * shareQuantity * priceHistory.lot}</p>

                <Button onClick={() => handleBuyStock({ticker: ticker, value: shareQuantity} as IStockTransaction)} type='primary'>
                    Купить
                </Button>
                <Button onClick={() => handleSellStock({ticker: ticker, value: shareQuantity} as IStockTransaction)} type='primary'>
                    Продать
                </Button>
            </Space>
        </div>
    );
};

export default Stock;