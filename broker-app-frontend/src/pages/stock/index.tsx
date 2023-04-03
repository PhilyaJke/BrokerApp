import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import {PriceDataList} from "../../interfaces/IPriceData";
import {CartesianGrid, Legend, Line, LineChart, Tooltip, XAxis, YAxis} from 'recharts';
import {format} from 'date-fns';

import appConfig from "../../../config";

const API_URL = appConfig.URL;


const Stock = () => {
    const {figi} = useParams<{ figi: string }>();
    const [data, setData] = useState<PriceDataList | null>(null);
    //data have format dd/mm/yyyy convert to new Date
    async function fetchData() {
        const response = await fetch(`${API_URL}/api/securities/list/stock/?figi=${figi}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        const data: PriceDataList = await response.json();
        setData(data);
    }


    useEffect(() => {
        fetchData();
    }, [figi]);

    //on start

    useEffect(() => {
            fetchData().then(r => console.log('try to fetch'))
        }
        , []);


    if (!data) {
        return <div>Loading...</div>;
    }

    return (
        <LineChart width={800} height={400} data={data} margin={{top: 20, right: 30, left: 20, bottom: 5}}>
            <CartesianGrid strokeDasharray="3 3"/>
            <XAxis dataKey="date"/>
            <YAxis/>
            <Tooltip/>
            <Legend/>
            <Line type="basis" dataKey="close" stroke="#8884d8" activeDot={{r: 8}}/>
        </LineChart>
    );
}


export default Stock;
