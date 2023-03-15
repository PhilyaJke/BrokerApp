import {useEffect, useState} from "react";
import useStocks from "../../hooks/useStocks/useStocks";
import {StocksCardProps} from "../../hooks/useStocks/useStocks.model";
import {Card, Radio} from "antd";
import Search from "antd/es/input/Search";
import {getRuStocks} from "../../hooks/useStocks/api";

//PRIVATE ROUTE

const OverviewPage = () => {
    const [stocksRegion, setStocksRegion] = useState<'RU' | 'FOREIGN' | 'ALL'>('RU');
    const {handleRuStocks, handleForeignStocks, handleAllStocks, isDown, isLoading} = useStocks();
    const [stocksCard, setStocksCard] = useState<StocksCardProps[]>([]);
    const getStocks = async () => {
        let stocks = [];
        switch (stocksRegion) {
            case 'RU':
                stocks = await handleRuStocks();
                break;
            case 'FOREIGN':
                stocks = await handleForeignStocks();
                break;
            case 'ALL':
                stocks = await handleAllStocks();
                break;
        }
        setStocksCard(stocks);
    };



    useEffect(() => {
        getStocks();
    }, [stocksRegion]);

    useEffect(() => {
        getStocks();
    }, []);

    // useEffect(() => {
    //     switch (stocksRegion) {
    //         case 'RU':
    //             setStocksCard(handleRuStocks());
    //             break;
    //         case 'FOREIGN':
    //             setStocksCard(handleRuStocks());
    //             break;
    //         case 'ALL':
    //             setStocksCard(handleRuStocks());
    //             break;
    //     }
    // }, [stocksRegion]);
    // }

    return (
        <div style={{display: 'flex', flexDirection: 'column', gap: '10px'}}>
            <h1>Overview</h1>
            <Search onSearch={() => {
            }} enterButton placeholder={'Поиск по тикеру'}/>
            <Radio.Group onChange={(v) => {
                setStocksRegion(v.target.value);
            }}>
                <Radio.Button value="RU">Российские акции 🇷🇺</Radio.Button>
                <Radio.Button value="FOREIGN">Иностранные акции</Radio.Button>
                <Radio.Button value="ALL">Все 🌍</Radio.Button>
            </Radio.Group>
            <p>{stocksRegion === 'RU' ? 'Российские акции' : stocksRegion === 'FOREIGN' ? 'Иностранные акции' : 'Все'}</p>
            <div style={{display: 'flex', gap: '10px', flexWrap: 'wrap'}}>
                {isLoading && <p>Загрузка...</p>}
                {isDown && <p>Сервер недоступен</p>}
                {!isLoading && !isDown && stocksCard.map((stock) => {
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