import {StocksCardProps} from './useStocks.model';


const useStocks = () => {
    const getRuStocks = (): StocksCardProps[] => {
        //TODO: implement
        return [
            {
                name: 'Автоваз',
                region: 'RU',
                sector: 'Автомобильный',
                ticker: 'AVAZ'
            } as StocksCardProps,
            {
                name: 'Газпром',
                region: 'RU',
                sector: 'Энергетика',
                ticker: 'GAZP'
            } as StocksCardProps,
            {
                name: 'Магнит',
                region: 'RU',
                sector: 'Торговля',
                ticker: 'MGNT'
            } as StocksCardProps,
            {
                name: 'М.видео',
                region: 'RU',
                sector: 'Торговля',
                ticker: 'MVID'
            } as StocksCardProps,
            {
                name: 'МТС',
                region: 'RU',
                sector: 'Телекоммуникации',
                ticker: 'MTSS'
            } as StocksCardProps,
            {
                name: 'Норникель',
                region: 'RU',
                sector: 'Металлургия',
                ticker: 'GMKN'
            } as StocksCardProps,
            {
                name: 'Роснефть',
                region: 'RU',
                sector: 'Энергетика',
                ticker: 'ROSN'
            } as StocksCardProps,
            {
                name: 'Сбербанк',
                region: 'RU',
                sector: 'Банки',
                ticker: 'SBER'
            } as StocksCardProps,
            {
                name: 'Сургутнефтегаз',
                region: 'RU',
                sector: 'Энергетика',
                ticker: 'SNGS'
            } as StocksCardProps,
            {
                name: 'Татнефть',
                region: 'RU',
                sector: 'Энергетика',
                ticker: 'TATN'
            } as StocksCardProps,
        ];

    }

    const getForeignStocks = (): StocksCardProps[] => {
        //TODO: implement
        return []
    }

    const getAllStocks = (): StocksCardProps[] => {
        //TODO: implement
        return []
    }

    return {
        getRuStocks,
        getForeignStocks,
        getAllStocks
    }
}


export default useStocks;
