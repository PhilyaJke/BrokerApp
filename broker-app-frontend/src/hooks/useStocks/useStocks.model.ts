export interface StocksCardProps {
    name: string;
    region: string;
    sector: string;
    ticker: string;
}



export interface StocksPageProps {
    "securitiesList": StocksCardProps[];
    "currentPage": number;
    "totalPages": number;
}
