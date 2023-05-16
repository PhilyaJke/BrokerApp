import {
    IPriceDataList,
    IPriceHistoryRequest,
    StocksCardProps,
    StocksPageProps,
    StocksPageRequest
} from './useStocks.model';


import appConfig from "../../../config";

const API_URL = appConfig.URL;
const WS_URL = appConfig.WS;


export const getStocks = async (props: StocksPageRequest): Promise<StocksPageProps> => {
    const {size, page, region} = props;
    console.log('getStocks region', region);
    let additionalParams = '';
    if (size) {
        if (additionalParams) {
            additionalParams += '&';
        }
        additionalParams += `size=${size}`;
    }
    if (page) {
        if (additionalParams) {
            additionalParams += '&';
        }
        additionalParams += `page=${page}`;
    }
    if (region && region !== 'ru') {
        if (additionalParams) {
            additionalParams += '&';
        }
        additionalParams += `region=${region}`;
    }

    const response = await fetch(`${API_URL}/api/securities/list/securities?` + additionalParams, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
    });

    if (!response.ok) {
        throw new Error('Failed to get ru stocks');
    }

    return response.json();
}

export const searchStocksForSuggestion = async (search: string): Promise<StocksCardProps[]> => {
    const response = await fetch(`${API_URL}/api/securities/list/search`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
                search
            }
        ),
    });

    if (!response.ok) {
        throw new Error('Failed to get ru stocks');
    }

    return response.json();
}

export const getRealtimeStockPrice = (ticker: string, callback: (price: number) => void): WebSocket => {
    const ws = new WebSocket(`${WS_URL}/price/${ticker}`);
    console.log('NEW WS CONNECTION', ticker);
    ws.onmessage = (event: MessageEvent) => {
        const { price } = JSON.parse(event.data);
        callback(price[0]);
    };
    return ws;
};


export const getStockPriceHistory = async (props: IPriceHistoryRequest, accessToken: string | null): Promise<IPriceDataList> => {
    const {ticker, from, to, interval} = props;
        const response = await fetch(
            `${API_URL}/api/securities/list/stock/?ticker=${ticker}`,
            {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    'Authorization': `${accessToken}`
                },
            }
        );
        if (!response.ok) {
            throw new Error("Failed to get stock price history");
        }
        return response.json();
}







    
