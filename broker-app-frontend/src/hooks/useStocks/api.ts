import {StocksCardProps} from './useStocks.model';

const API_URL = 'http://localhost:8080';

    export const getAllStocks = async (): Promise<StocksCardProps[]> => {
        const response = await fetch(`${API_URL}/api/securities/list/allsecurities`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) {
            throw new Error('Failed to get all stocks');
        }

        return response.json();
    };

    export const getRuStocks = async (): Promise<StocksCardProps[]> => {
        const response = await fetch(`${API_URL}/api/securities/list/allrusecurities`, {
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


    export const getForeignStocks = async (): Promise<StocksCardProps[]> => {
        const response = await fetch(`${API_URL}/api/securities/list/allforeignsecurities`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) {
            throw new Error('Failed to get foreign stocks');
        }

        return response.json();
    }

    
