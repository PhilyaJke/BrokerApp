import {IStockTransaction} from "./useStockTransactions.model";
import {useState} from "react";
import {buyStocks, sellStocks} from "./api";
import {useTokens} from "../../providers/authProvider/authProvider";

const useStockTransactions = () => {
    const {accessToken} = useTokens();
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [error, setError] = useState<string>('');
    const handleBuyStock = async (props: IStockTransaction) => {
        try {
            setIsLoading(true);
            const response = await buyStocks(props, accessToken);
            setIsLoading(false)
            return response;
        } catch (e) {
            setIsLoading(false)
            console.log(e);
        }
    }
    const handleSellStock = async (props: IStockTransaction) => {
        try {
            setIsLoading(true);
            const response = await sellStocks(props, accessToken);
            setIsLoading(false)
            return response;
        } catch (e) {
            setIsLoading(false)
            console.log(e);
        }
    }


    return {
        isLoading,
        error,
        handleBuyStock,
        handleSellStock
    }
}

export default useStockTransactions;