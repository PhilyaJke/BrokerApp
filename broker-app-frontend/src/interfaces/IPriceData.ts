export interface IPriceData {
    date: string;
    low: number;
    high: number;
    close: number;
    open: number;
}

export type PriceDataList = IPriceData[];

