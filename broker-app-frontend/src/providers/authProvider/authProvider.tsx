import React, {createContext, useContext, useEffect, useState} from 'react';
import jwtDecode from 'jwt-decode';
import {login, refreshAccessToken, register} from './api';
import AuthTokens, {LoginReq, RegisterReq, TokenContextValue} from "./models";


const refreshTokenLocalStorageKey = 'ref_t';


const TokenContext = createContext<TokenContextValue | null>(null);

export function TokenProvider({children}: { children: React.ReactNode }) {
    const [tokens, setTokens] = useState<AuthTokens>({
        accessToken: null,
        refreshToken: localStorage.getItem(refreshTokenLocalStorageKey),
    });

    useEffect(() => {
        localStorage.setItem(refreshTokenLocalStorageKey, tokens.refreshToken || '');
    }, [tokens.refreshToken]);

    return (
        <TokenContext.Provider value={{tokens, setTokens}}>
            {children}
        </TokenContext.Provider>
    );
}

export function useTokens() {
    const context = useContext(TokenContext);

    if (!context) {
        throw new Error('useTokens must be used within a TokenProvider');
    }

    return context.tokens;
}

export function useIsAuthenticated() {
    const tokens = useTokens();
    if (!tokens.accessToken) {
        return false;
    }

    const expirationTime = jwtDecode<{ exp: number }>(tokens.accessToken).exp;
    return Date.now() < expirationTime;
}

interface AuthContextValue {
    login: (req: LoginReq) => Promise<void>;
    register: (req: RegisterReq) => Promise<void>;
    logout: () => void;
    username: string | null;
    isDown: boolean;
    isLoading: boolean;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({children}: { children: React.ReactNode }) {
    const {tokens, setTokens} = useContext(TokenContext)!;
    const [username, setUsername] = useState<string | null>(null);
    const [isDown, setIsDown] = useState<boolean>(false);
    const [isLoading, setIsLoading] = useState<boolean>(true);


    //Отвечает за авторизацию при перезагрузке страницы
    useEffect(() => {
        if (!tokens.refreshToken) {
            setIsLoading(false);
            return;
        }
        handleRefreshAccessToken().finally(() => setIsLoading(false));
    }, []);

    useEffect(() => {
        if (!tokens.accessToken) {
            setUsername(null);
            return;
        }
        const {username} = jwtDecode<{ username: string }>(tokens.accessToken);
        setUsername(username);
    }, [tokens.accessToken]);

    const handleRefreshAccessToken = async () => {
        try {
            const {refreshToken} = tokens;

            if (!refreshToken) {
                return;
            }

            const {accessToken} = await refreshAccessToken(refreshToken);
            setTokens({accessToken, refreshToken});
        } catch (error) {
            console.error(`Failed to refresh access token: ${error}`);
            setIsDown(true);
        }
    };

    useEffect(() => {
        if (!tokens.accessToken) {
            return;
        }
        const expirationTime = jwtDecode<{ exp: number }>(tokens.accessToken).exp;
        const refreshTime = (expirationTime - Date.now()) * 0.9;
        const interval = setInterval(() => {
            handleRefreshAccessToken();
        }, refreshTime);
        return () => clearInterval(interval);
    }, [tokens.accessToken]);

    const handleLogin = async (req: LoginReq) => {
        try {
            const {accessToken, refreshToken, username} = await login(req);
            setTokens({accessToken, refreshToken});
            setUsername(username);
        } catch (error) {
            console.error(`Failed to login: ${error}`);
            setIsDown(true);
        }
    };

    const handleRegister = async (req: RegisterReq) => {
        try {
            const {accessToken, refreshToken, username} = await register(req);
            setTokens({accessToken, refreshToken});
            setUsername(username);
        } catch (error) {
            console.error(`Failed to register: ${error}`);
            setIsDown(true);
        }
    };

    const handleLogout = () => {
        setUsername(null);
        setTokens({accessToken: null, refreshToken: null});
        localStorage.removeItem(refreshTokenLocalStorageKey);
    };

    return (
        <AuthContext.Provider
            value={{login: handleLogin, register: handleRegister, logout: handleLogout, username, isDown, isLoading}}
        >
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const context = useContext(AuthContext);

    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }

    return context;
}
