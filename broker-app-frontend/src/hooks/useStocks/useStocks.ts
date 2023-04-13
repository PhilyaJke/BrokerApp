import {useState} from "react";
import {getRealtimeStockPrice, getStocks, searchStocksForSuggestion} from "./api";
import {StocksCardProps, StocksPageProps, StocksPageRequest} from "./useStocks.model";


const useStocks = () => {
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [isDown, setIsDown] = useState<boolean>(false);
    const [totalPages, setTotalPages] = useState<number>(1);


    const handleStocks = async (props: StocksPageRequest): Promise<StocksCardProps[]> => {
        setIsLoading(true);
        try {
            const stocksPage: StocksPageProps = await getStocks(props);
            console.log('handleStocks stocksPage', props.region);
            setIsLoading(false);
            setIsDown(false)
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

    const handleRealtimePrice = (ticker: string, callback: (price: number) => void): () => void => {

        const ws = getRealtimeStockPrice(ticker, (price: number) => {
            callback(price);
        });

        return () => {
            ws.close();
        };

    }
    return {
        isLoading,
        isDown,
        handleStocks,
        handleSearchForSuggestions,
        totalPages,
        handleRealtimePrice,
    };
};

export default useStocks;
