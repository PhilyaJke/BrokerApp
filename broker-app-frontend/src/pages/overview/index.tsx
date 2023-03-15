import {useEffect, useState} from "react";
import useStocks from "../../hooks/useStocks/useStocks";
import {StocksCardProps} from "../../hooks/useStocks/useStocks.model";
import {Card, Radio} from "antd";
import Search from "antd/es/input/Search";

//PRIVATE ROUTE

const OverviewPage = () => {
    const [stocksRegion, setStocksRegion] = useState<'RU' | 'FOREIGN' | 'ALL'>('RU');
    const {getRuStocks, getAllStocks, getForeignStocks} = useStocks();
    const [stocksCard, setStocksCard] = useState<StocksCardProps[]>(getRuStocks());

    useEffect(() => {
        switch (stocksRegion) {
            case 'RU':
                setStocksCard(getRuStocks());
                break;
            case 'FOREIGN':
                setStocksCard(getForeignStocks());
                break;
            case 'ALL':
                setStocksCard(getAllStocks());
                break;
        }
    }, [stocksRegion]);
    return (
        <div style={{display: 'flex', flexDirection: 'column', gap: '10px'}}>
            <h1>Overview</h1>
            <Radio.Group onChange={() => {
            }}>
                <Radio.Button value="RU">Российские акции 🇷🇺</Radio.Button>
                <Radio.Button value="FOREIGN">Иностранные акции</Radio.Button>
                <Radio.Button value="ALL">Все 🌍</Radio.Button>
            </Radio.Group>
            <Search onSearch={() => {
            }} enterButton placeholder={'Поиск по тикеру'}/>
            <p>{stocksRegion === 'RU' ? 'Российские акции' : 'Зарубежные акции'}</p>
            <div style={{display: 'flex', gap: '10px', flexWrap: 'wrap'}}>
                {stocksCard.map((stock) => {
                    return (
                        <Card
                            key={stock.name}
                            title={stock.name}
                            extra={<a href="#">More</a>}
                            style={{width: 200}}
                        >
                            <p>{stock.region}</p>
                            <p>{stock.sector}</p>
                            <code>{stock.ticker}</code>
                        </Card>
                    )
                })
                }
            </div>
        </div>
    )
}


export default OverviewPage