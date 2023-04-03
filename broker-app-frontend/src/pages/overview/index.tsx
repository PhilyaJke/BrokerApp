import {memo, useCallback, useEffect, useMemo, useRef, useState} from "react";
import useStocks from "../../hooks/useStocks/useStocks";
import {StocksCardProps, StocksPageRequest} from "../../hooks/useStocks/useStocks.model";
import {AutoComplete, Button, Card, Radio} from "antd";
import Search from "antd/es/input/Search";
import AppLoader from "../../components/appLoader";
//use redirect
import {Link, useNavigate} from "react-router-dom";


//плашка для показа что все акции загружены
const EndOfStocks = () => {
    return (
        <div style={{display: 'flex', justifyContent: 'center', alignItems: 'center', width: '100%'}}>
            <Card style={{width: 300}}>
                <p>Конец списка</p>
            </Card>
        </div>
    );
};


const StocksCard = memo(({ticker, name, region, sector, price, icon_path}: StocksCardProps) => {
    const navigate = useNavigate();
    return (
        <Card title={ticker} style={{width: 300}} onClick={() => navigate(`/quote/${ticker}`)}>
            <img src={icon_path} style={{width: 50, height: 50}} alt={''}/>
            <p><b>{name}</b></p>
            <p>{region}</p>
            <p>{sector}</p>
            <p>{String(price)}$</p>
        </Card>
    );
});


const OverviewPage = () => {
    const [stocksRegion, setStocksRegion] = useState<'ru' | 'foreign' | 'all'>('ru');
    const {handleStocks, isDown, isLoading, totalPages, handleSearchForSuggestions} = useStocks();
    const [stocksCard, setStocksCard] = useState<StocksCardProps[]>([]);
    const [seachValue, setSearchValue] = useState<string>('');
    const [searchResult, setSearchResult] = useState<StocksCardProps[]>([]);
    const [isEnd, setIsEnd] = useState<boolean>(false);
    const size = 40;
    const [page, setPage] = useState(0);
    const [maxPage, setMaxPage] = useState(1);
    const loaderRef = useRef<HTMLDivElement>(null);
    const navigate = useNavigate();
    const handleChangeSearch = (e: any) => {
        console.log(e);
        setSearchValue(e);
    }


    useEffect(() => {
        if (seachValue === '') {
            setSearchResult([]);
        } else {
            searchStocksForSuggestions(seachValue);
        }
    }, [seachValue]);

    const searchSuggestions = useMemo(() => {
        return searchResult.map((stock) => {
            return {
                value: stock.ticker,
                label: (<div style={{display:'flex', justifyContent:'start', alignItems: 'center', gap: '8px'}} onClick={() => navigate(`/quote/${stock.ticker}`)}>
                    <img style={{width: 32, height: 32}} src={stock.icon_path}/>
                    <div style={{display: 'flex', flexDirection: 'column'}}>
                        <span><code>{stock.ticker}</code></span>
                        <span><b>{stock.name}</b></span>
                    </div>
                </div>),
            };
        });
    }, [searchResult]);

    const searchStocksForSuggestions = useCallback((value: string) => {
        handleSearchForSuggestions(value).then((res) => {
            setSearchResult(res);
        });
    }, [handleSearchForSuggestions]);


    const handleChangeRegion = (e: 'ru' | 'foreign' | 'all') => {
        console.log(e);
        setStocksRegion(e);
        setPage(0);
    }


    const getStocks = async () => {
        const stocks = await handleStocks({page, size, region: stocksRegion} as StocksPageRequest);
        if (!Boolean(stocks) || stocks.length === 0) {
            setIsEnd(true);
            return;
        }
        setMaxPage(maxPage);
        if (page === 0) {
            setStocksCard(stocks);
            return;
        }
        setStocksCard(prevStocks => [...prevStocks, ...stocks]);
    };


    const stocksList = useMemo(() => {
        return stocksCard.map((stock, index) => {
            if (isEnd && index === stocksCard.length - 1) return (<EndOfStocks key={index}/>);

            if (index === stocksCard.length - 1) {
                return (
                    <AppLoader key={index}/>
                );
            }
            return <StocksCard key={index} {...stock}/>;
        });
    }, [stocksCard, page, stocksRegion, loaderRef]);

    useEffect(() => {
        if (isEnd) setIsEnd(false);
        getStocks()
    }, [stocksRegion]);

    useEffect(() => {
        if (isEnd) return;
        getStocks();
    }, [page]);

    useEffect(() => {
        getStocks();
        window.addEventListener("scroll", handleScroll);
        return () => {
            window.removeEventListener("scroll", handleScroll);
        };
    }, []);

    const handleScroll = () => {
        if (isEnd) return;
        const {scrollHeight, scrollTop, clientHeight} = document.documentElement;
        if (scrollTop + clientHeight >= scrollHeight * 0.9) {
            setPage(prevPage => prevPage + 1);
        }
    }

    return (
        <div style={{display: 'flex', flexDirection: 'column', gap: '10px'}}>
            <h1>Overview</h1>
            <AutoComplete
                options={searchSuggestions}
                onSelect={(value) => {
                    setSearchValue(value);
                    setSearchResult([]);
                }
                }
                onSearch={(value) => {
                    setSearchValue(value);
                }
                }
            >
                <Search placeholder="Поиск по тикеру" enterButton/>
                <Button>Поиск</Button>
            </AutoComplete>
            <Radio.Group onChange={(e) => handleChangeRegion(e.target.value)} value={stocksRegion}>
                <Radio.Button value="ru">Российские акции 🇷🇺</Radio.Button>
                <Radio.Button value="foreign">Иностранные акции</Radio.Button>
                <Radio.Button value="all">Все 🌍</Radio.Button>
            </Radio.Group>
            <p>{stocksRegion === 'ru' ? 'Российские акции' : stocksRegion === 'foreign' ? 'Иностранные акции' : 'Все'}</p>
            {isDown && <p>Сервер недоступен</p>}
            <div style={{display: 'flex', gap: '10px', flexWrap: 'wrap'}}>
                {stocksList}
            </div>
        </div>
    )
}


export default OverviewPage;