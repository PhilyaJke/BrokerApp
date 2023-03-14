import load from '../../assets/load.webp';
import styled from "styled-components";
import {useEffect, useRef} from "react";

const AppLoader = () => {
    const loaderText = useRef<HTMLParagraphElement>(null);
    const timer = useRef(0);

    useEffect(() => {
        const intervalId = setInterval(() => {
            timer.current += 300;
            if (loaderText.current) {
                if (timer.current > 0) {
                    loaderText.current.innerText = "Узнаем цены на биржах...";
                }
                if (timer.current > 1400) {
                    loaderText.current.innerText = "Получаем разрешение Набиулиной...";
                }
                if (timer.current > 3000) {
                    loaderText.current.innerText = "Дальше не придумал, но бэк лежит...";
                }
            }
        }, 300);

        return () => clearInterval(intervalId);
    }, []);

    return (
        <LoaderWrapper>
            <LoaderImage src={load} alt="загрузка..."/>
            <p ref={loaderText}></p>
        </LoaderWrapper>
    );
};


const LoaderWrapper = styled.div`
  //in display center
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100vw;
  height: 100vh;

`

const LoaderImage = styled.img`
  width: 5vw;
  height: 5vw;
`


export default AppLoader