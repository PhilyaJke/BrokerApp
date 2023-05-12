import {StocksCardProps} from "../useStocks/useStocks.model";

export interface IProfile {
    username: string;
    securities: StocksCardProps[];
}