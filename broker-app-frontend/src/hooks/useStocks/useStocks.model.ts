export interface StocksCardProps {
    name: string;
    region: string;
    sector: string;
    ticker: string;
    price: number;
    figi: string;
    icon_path: string;
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
