export interface IPriceData {
    date: Date;
    low: number;
    high: number;
    close: number;
    open: number;
}

export type PriceDataList = IPriceData[];

