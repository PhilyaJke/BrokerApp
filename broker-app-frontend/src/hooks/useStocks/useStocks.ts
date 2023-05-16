import {useState} from "react";
import {getRealtimeStockPrice, getStockPriceHistory, getStocks, searchStocksForSuggestion} from "./api";
import {
    IPriceHistoryRequest,
    StocksCardProps,
    StocksPageProps,
    StocksPageRequest, IPriceDataList
} from "./useStocks.model";
import {useTokens} from "../../providers/authProvider/authProvider";


const useStocks = () => {
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [isDown, setIsDown] = useState<boolean>(false);
    const [totalPages, setTotalPages] = useState<number>(1);
    const {accessToken} = useTokens();

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


    const handlePriceHistory = async (props: IPriceHistoryRequest): Promise<IPriceDataList> => {
        setIsLoading(true);
        try {
            const priceDataList: IPriceDataList = await getStockPriceHistory(props, accessToken);
            setIsLoading(false);
            setIsDown(false)
            return priceDataList
        }
        catch (e) {
            setIsLoading(false);
            setIsDown(true);
            return {
                lot: 0,
                candles: []
            };
        }
    }
    return {
        isLoading,
        isDown,
        handleStocks,
        handleSearchForSuggestions,
        totalPages,
        handleRealtimePrice,
        handlePriceHistory
    };
};

export default useStocks;
