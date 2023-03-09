import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'
import {darkTheme} from "./styles/themes/dark.theme";
import {GlobalStyle} from "./styles/globalStyles";
import RegisterPage from "./pages/register";
import {ThemeProvider} from "styled-components";
import {BrowserRouter} from "react-router-dom";
import {AuthProvider} from "./providers/authProvider";

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
    <React.StrictMode>
        <AuthProvider>
    <ThemeProvider theme={darkTheme}>
        <GlobalStyle/>
        <BrowserRouter>
    <App />
        </BrowserRouter>
    </ThemeProvider>
        </AuthProvider>
        </React.StrictMode>
)
