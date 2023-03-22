import {useState} from "react";
import {getStocks, searchStocksForSuggestion} from "./api";
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
            console.log('handleStocks stocksPage', props.region);
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

    const handleSearchForSuggestions = async (search: string): Promise<StocksCardProps[]> => {
        setIsLoading(true);
        try {
            const stocks: StocksCardProps[] = await searchStocksForSuggestion(search);
            setIsLoading(false);
            setIsDown(false)
            return stocks
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
        handleSearchForSuggestions,
        totalPages,
    };
};

export default useStocks;
