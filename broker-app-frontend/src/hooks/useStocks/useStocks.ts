import { useState } from "react";
import {getStocks} from "./api";
import {StocksCardProps, StocksPageProps, StocksPageRequest} from "./useStocks.model";


const useStocks = () => {
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [isDown, setIsDown] = useState<boolean>(false);
    const [page, setPage] = useState<number>(0);
    const [totalPages, setTotalPages] = useState<number>(1);



    const handleStocks = async (props: StocksPageRequest): Promise<StocksCardProps[]> => {
        setIsLoading(true);
        try {
            const stocksPage: StocksPageProps = await getStocks(props);
            setIsLoading(false);
            setIsDown(false)
            setTotalPages(stocksPage.totalPages);
            return stocksPage.securitiesList
        } catch (e) {
            setIsLoading(false);
            setIsDown(true);
            return [];
        }
    }

    return {
        isLoading,
        isDown,
        handleStocks,
        totalPages,
    };
};

export default useStocks;
