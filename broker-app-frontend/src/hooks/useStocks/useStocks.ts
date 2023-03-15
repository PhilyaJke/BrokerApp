import { useState } from "react";
import { getAllStocks, getForeignStocks, getRuStocks } from "./api";
import { StocksCardProps } from "./useStocks.model";

const useStocks = () => {
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [isDown, setIsDown] = useState<boolean>(false);

    const handleRuStocks = async (): Promise<StocksCardProps[]> => {
        setIsLoading(true);
        try {
            const stocks = await getRuStocks();
            setIsLoading(false);
            setIsDown(false)
            return stocks;
        } catch (e) {
            setIsLoading(false);
            setIsDown(true);
            return [];
        }
    };

    const handleForeignStocks = async (): Promise<StocksCardProps[]> => {
        setIsLoading(true);
        try {
            const stocks = await getForeignStocks();
            setIsLoading(false);
            setIsDown(false);
            return stocks;
        } catch (e) {
            setIsLoading(false);
            setIsDown(true);
            return [];
        }
    };

    const handleAllStocks = async (): Promise<StocksCardProps[]> => {
        setIsLoading(true);
        try {
            const stocks = await getAllStocks();
            setIsLoading(false);
            setIsDown(false)
            return stocks;
        } catch (e) {
            setIsLoading(false);
            setIsDown(true);
            return [];
        }
    };

    return {
        isLoading,
        isDown,
        handleRuStocks,
        handleForeignStocks,
        handleAllStocks,
    };
};

export default useStocks;
