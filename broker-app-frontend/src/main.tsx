import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'
import {darkTheme} from "./styles/themes/dark.theme";
import {GlobalStyle} from "./styles/globalStyles";
import styled, {ThemeProvider} from "styled-components";
import {BrowserRouter} from "react-router-dom";
import {AuthProvider, TokenProvider} from "./providers/authProvider/authProvider";


const AppWrapper = styled.div`
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 0 auto;
`

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
    //<React.StrictMode>
        <TokenProvider>
            <AuthProvider>
                <ThemeProvider theme={darkTheme}>
                    <GlobalStyle/>
                    <BrowserRouter>
                        <AppWrapper>
                            <App/>
                        </AppWrapper>
                    </BrowserRouter>
                </ThemeProvider>
            </AuthProvider>
        </TokenProvider>
    //</React.StrictMode>
)
