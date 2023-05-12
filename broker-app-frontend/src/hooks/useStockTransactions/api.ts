import {IStockTransaction} from "./useStockTransactions.model";
import appConfig from "../../../config";
const API_URL = appConfig.URL;


export const buyStocks = async (props: IStockTransaction, accessToken: string | null): Promise<void> => {
    console.log('buyStocks called');
    const {ticker, value} = props;
    ///api/buySecurity
    const response = await fetch(`${API_URL}/api/buySecurity`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `${accessToken}`
        }
        ,
        body: JSON.stringify({
                ticker,
                value
        }),
    });
    return response.json();
}

export const sellStocks = async (props: IStockTransaction, accessToken: string | null): Promise<void> => {
    console.log('sellStocks called');
    const {ticker, value} = props;
    ///api/sellSecurity
    const response = await fetch(`${API_URL}/api/sellSecurity`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `${accessToken}`
        },
        body: JSON.stringify({
                ticker,
                value
        }),
    });
    return response.json();
}