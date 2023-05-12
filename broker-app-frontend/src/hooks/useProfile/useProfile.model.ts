import {StocksCardProps} from "../useStocks/useStocks.model";

export interface IProfile {
    username: string;
    budjet: number;
    securities: StocksCardProps[];
}