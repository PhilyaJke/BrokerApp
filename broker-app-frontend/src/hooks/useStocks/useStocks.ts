import { useState } from "react";
import { getAllStocks, getForeignStocks, getRuStocks } from "./api";
import { StocksCardProps, StocksPageProps } from "./useStocks.model";

const useStocks = () => {
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [isDown, setIsDown] = useState<boolean>(false);
    const [page, setPage] = useState<number>(0);
    const [size, setSize] = useState<number>(10);
    const [totalPages, setTotalPages] = useState<number>(0);



    const handleRuStocks = async (): Promise<StocksCardProps[]> => {
        setIsLoading(true);
        try {
            const stocksPage: StocksPageProps = await getRuStocks();
            setIsLoading(false);
            setIsDown(false);
            return stocksPage.securitiesList;
            console.log(stocksPage.securitiesList)
        } catch (e) {
            setIsLoading(false);
            setIsDown(true);
            return [];
        }
    };

    const handleForeignStocks = async (): Promise<StocksCardProps[]> => {
        setIsLoading(true);
        try {
            const stocksPage: StocksPageProps = await getForeignStocks();
            setIsLoading(false);
            setIsDown(false);
            return stocksPage.securitiesList;
            console.log(stocksPage.securitiesList)
        } catch (e) {
            setIsLoading(false);
            setIsDown(true);
            return [];
        }
    };

    const handleAllStocks = async (): Promise<StocksCardProps[]> => {
        setIsLoading(true);
        try {
            const stocksPage: StocksPageProps = await getAllStocks();
            setIsLoading(false);
            setIsDown(false)
            return stocksPage.securitiesList;
            console.log(stocksPage.securitiesList)
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
