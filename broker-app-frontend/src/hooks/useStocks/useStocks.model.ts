export interface StocksCardProps {
    name: string;
    region: string;
    sector: string;
    ticker: string;
    price: number;
    iconPath: string;
}


export interface StocksPageRequest {
    page?: number;
    size?: number;
    region?: 'ru' | 'foreign' | 'all';
}


export interface StocksPageProps {
    "securitiesList": StocksCardProps[];
    // "currentPage": number;
    // "totalPages": number;
}

export interface IPriceDataList {
    lot: number;
    candles: ICandle[];
}

export interface ICandle {
    date: Date;
    low: number;
    high: number;
    close: number;
    open: number;
}

export interface IPriceHistoryRequest {
    ticker: string;
    from?: Date;
    to?: Date;
    interval?: '5min' | '10min' | '15min' | '30min' | 'hour' | '1d' | '7d' | '30d' | '90d' | '360d';
}



