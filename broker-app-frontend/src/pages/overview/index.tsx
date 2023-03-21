import {useEffect, useState, useRef, memo, useCallback, useMemo} from "react";
import useStocks from "../../hooks/useStocks/useStocks";
import {StocksCardProps, StocksPageRequest} from "../../hooks/useStocks/useStocks.model";
import {Card, Radio} from "antd";
import Search from "antd/es/input/Search";
import {getStocks} from "../../hooks/useStocks/api";
import AppLoader from "../../components/appLoader";
import {json} from "react-router-dom";

//PRIVATE ROUTE

const StocksCard = memo(({ticker, name, region, sector, lastprice}: StocksCardProps) => {
    return (
        <Card title={ticker} style={{width: 300}}>
            <p>{name}</p>
            <p>{region}</p>
            <p>{sector}</p>
            <p>{String(lastprice)}$</p>
        </Card>
    );
});


const OverviewPage = () => {
    const [stocksRegion, setStocksRegion] = useState<'ru' | 'foreign' | 'all'>('ru');
    const {handleStocks, isDown, isLoading, totalPages} = useStocks();
    const [stocksCard, setStocksCard] = useState<StocksCardProps[]>([]);
    const size = 40;
    const [page, setPage] = useState(0);
    const [maxPage, setMaxPage] = useState(1);
    const loaderRef = useRef<HTMLDivElement>(null);
    let price = 'test'
    //random price each 10 seconds
    setInterval(() => {
    price = Math.floor(Math.random() * 1000).toString();
            console.log('price', price)
    }
    , 10000);

    const handleChangeRegion = (e: 'ru' | 'foreign' | 'all') => {
        console.log(e);
        setStocksRegion(e);
        setPage(0);
    }


    const getStocks = async () => {
        const stocks = await handleStocks({page, size, region: stocksRegion} as StocksPageRequest);
        setMaxPage(maxPage);
        //if region change, clear stocksCard
        if (page === 0) {
            setStocksCard(stocks);
            return;
        }
        setStocksCard(prevStocks => [...prevStocks, ...stocks]);
    };



    // const handleObserver = useCallback((entries: any[]) => {
    //     const target = entries[0];
    //     if (target.isIntersecting) {
    //         setPage(prevPage => prevPage + 1);
    //         console.log('page', page);
    //     }
    // }, []);
    //
    // const observer = useMemo(() => {
    //     return new IntersectionObserver(handleObserver, {threshold: 0.75});
    // }, [handleObserver, loaderRef]);


    const stocksList = useMemo(() => {
        return stocksCard.map((stock, index) => {
            if (index === stocksCard.length - 1) {
                return (
                    <AppLoader key={index}/>
                );
            }
            //TODO REMOVE TEST PRICE
            return <StocksCard key={index} {...stock}/>;
        });
    }, [stocksCard, page, stocksRegion, loaderRef]);

    // useEffect(() => {
    //     const loader = loaderRef.current;
    //     if (loader) {
    //         observer.observe(loader);
    //     }
    //     return () => {
    //         if (loader) {
    //             observer.unobserve(loader);
    //         }
    //     };
    // }, [observer, loaderRef]);

    useEffect(() => {
        getStocks();
    }, [page, stocksRegion]);

    useEffect(() => {
        getStocks();
        window.addEventListener("scroll", handleScroll);
        return () => {
            window.removeEventListener("scroll", handleScroll);
        };
    }, []);

    const handleScroll = () => {
        const {scrollHeight, scrollTop, clientHeight} = document.documentElement;
        if (scrollTop + clientHeight >= scrollHeight * 0.9) {
            setPage(prevPage => prevPage + 1);
        }
    }

    return (
        <div style={{display: 'flex', flexDirection: 'column', gap: '10px'}}>
            <h1>Overview</h1>
            <Search onSearch={() => {
            }} enterButton placeholder={'Поиск по тикеру'}/>
            <Radio.Group onChange={(e) => handleChangeRegion(e.target.value)} value={stocksRegion}>
                <Radio.Button value="ru">Российские акции 🇷🇺</Radio.Button>
                <Radio.Button value="foreign">Иностранные акции</Radio.Button>
                <Radio.Button value="all">Все 🌍</Radio.Button>
            </Radio.Group>
            <p>{stocksRegion === 'ru' ? 'Российские акции' : stocksRegion === 'foreign' ? 'Иностранные акции' : 'Все'}</p>
            <div style={{display: 'flex', gap: '10px', flexWrap: 'wrap'}}>
                {isLoading && <p>Загрузка...</p>}
                {isDown && <p>Сервер недоступен</p>}
                {/*{!isLoading && !isDown && stocksCard.map((stock, index) => {*/}
                {/*    if (index === stocksCard.length - 1) {*/}
                {/*        return (*/}
                {/*            <div key={index} ref={loaderRef}>*/}
                {/*                Загрузка...*/}
                {/*            </div>*/}
                {/*        );*/}
                {/*    }*/}
                {/*    return (*/}
                {/*        <StocksCard key={index} {...stock}/>*/}
                {/*    )*/}
                {/*})}*/}
                {stocksList}
            </div>
        </div>
    )
}


export default OverviewPage;