import {StocksCardProps, StocksPageProps, StocksPageRequest} from './useStocks.model';

const API_URL = 'http://localhost:8080';


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





    
