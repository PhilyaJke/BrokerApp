import React, {createContext, useContext, useEffect, useState} from "react";
import jwtDecode from "jwt-decode";

const config = {
    authUrl: 'http://localhost:8080',
    refreshTokenLocalStorageKey: 'ref_t'
}

interface LoginReq {
    email: string,
    password: string
}

interface RegisterReq {
    username: string,
    email: string,
    password: string
}

interface AuthRes {
    username: string;
    accessToken: string,
    refreshToken: string
}

interface contextRes {
    status: string,
    message: string,
}

interface refreshAccessTokenRes {
    accessToken: string,
}

interface registerReq {
    username: string,
    email: string,
    password: string
}

interface Context {
    login: (props: LoginReq) => Promise<contextRes>,
    register: (props: RegisterReq) => Promise<contextRes>,
    logout: () => void,
    getAccessToken: () => string | null,
    isAuth: boolean,
    isReady: boolean,
    isDown: boolean,
}


const AuthContext = createContext<Context>({
    login: () => Promise.resolve({status: 'error', message: 'something went wrong'}),
    register: () => Promise.resolve({status: 'error', message: 'something went wrong'}),
    logout: () => Promise.resolve({status: 'error', message: 'something went wrong'}),
    getAccessToken: () => null,
    isAuth: false,
    isReady: false,
    isDown: false,
});


export const AuthProvider = ({children}: { children: React.ReactNode }) => {
    const [accessToken, setAccessToken] = useState<string | null>(null);
    const [refreshToken, setRefreshToken] = useState<string | null>(localStorage.getItem(config.refreshTokenLocalStorageKey) || null);
    const [isReady, setIsReady] = useState<boolean>(false);
    const [isDown, setIsDown] = useState<boolean>(false);


    // Controllers


    //on start get refresh token from localstorage
    useEffect(() => {
        const accessTokenFromLS = localStorage.getItem(config.refreshTokenLocalStorageKey);
        if (accessTokenFromLS) {
            refreshAccessToken(accessTokenFromLS)
                .then((res) => {
                    if (res === 401) {
                        logout();
                    }
                })
                .finally(() => {
                        setIsReady(true);
                    }
                );
        } else {
            setIsReady(true);
        }

    }, []);


    useEffect(() => {
        localStorage.setItem(config.refreshTokenLocalStorageKey, refreshToken || '');
    }, [refreshToken]);


    const refreshAccessToken = async (refreshToken: string): Promise<number> => {
        try {
            const response = await fetch(`${config.authUrl}/api/auth/updateaccesstoken`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${refreshToken}`
                }
            });
            if (!response.ok) {
                return response.status;
            }
            const data: refreshAccessTokenRes = await response.json();
            setAccessToken(data.accessToken);
            return response.status;
        } catch (e) {
            console.log(e);
            setIsDown(true);
            return 500;
        }
    };


    //auto refresh access token every XX minutes if user is logged in
    useEffect(() => {
        const refreshTokenFromLS = localStorage.getItem(config.refreshTokenLocalStorageKey);

        if (!accessToken || !refreshTokenFromLS) {
            return;
        }

        const expirationTime = jwtDecode<{ exp: number }>(accessToken).exp;
        console.log('expirationTime', expirationTime - Date.now());
        console.log('Date.now()', Date.now(), expirationTime);
        const refreshTime = (expirationTime - Date.now()) * 0.9; //рассчитываем время до обновления токена в мс (90% от времени жизни токена)
        console.log('refreshTime', refreshTime);
        const interval = setInterval(() => {
            refreshAccessToken(refreshTokenFromLS || '');
        }, refreshTime);

        return () => clearInterval(interval);
    }, [accessToken]);


    async function login(props: LoginReq): Promise<contextRes> {
        try {
            const response = await fetch(`${config.authUrl}/api/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(props),
            });

            if (response.status === 500) {
                setIsDown(true);
                return {status: 'error', message: 'Сервис временно недоступен'};
            }

            const data: AuthRes = await response.json();

            if (!data.refreshToken) {
                return {status: 'error', message: 'Неверный логин или пароль'};
            }

            setRefreshToken(data.refreshToken);
            setAccessToken(data.accessToken);
            return {status: 'ok', message: 'ok'};
        } catch (error) {
            console.error(`Failed to login: ${error}`);
            setIsDown(true)
            return {status: 'error', message: 'Сервис временно недоступен'};
        }
    }


    const logout = async () => {
        setAccessToken(null);
        setRefreshToken(null);
        localStorage.removeItem(config.refreshTokenLocalStorageKey);
        try {
            await fetch(config.authUrl + "/api/auth/logout", {
                method: "GET",
                headers: {
                    "Authorization": `Bearer ${refreshToken}`,
                    "Content-Type": "application/json"
                },
            })
        } catch (e) {
            console.log(e);
        }
    }

    const register = async (props: registerReq): Promise<contextRes> => {
        try {
            const response = await fetch(`${config.authUrl}/api/auth/registration`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(props),
            });

            const data: AuthRes = await response.json();

            if (!data.refreshToken) {
                return {status: 'error', message: 'Неверный логин или пароль'};
            }

            setRefreshToken(data.refreshToken);
            setAccessToken(data.accessToken);
            return {status: 'ok', message: 'ok'};
        } catch (error) {
            console.error(`Failed to reg: ${error}`);
            setIsDown(true);
            return {status: 'error', message: 'Сервис временно недоступен'};
        }
    };


    const getAccessToken = (): string | null => {
        return accessToken;
    }

    const isAuth = (): boolean => {
        if (!accessToken) {
            return false;
        }
        const expirationTime = jwtDecode<{ exp: number }>(accessToken).exp;
        return Date.now() < expirationTime;
    }

    const getIsReady = () => {
        return isReady;
    }

    const getIsDown = () => {
        return isDown;
    }


    const value: Context = {
        login,
        register,
        logout,
        getAccessToken,
        isAuth: isAuth(),
        isDown: getIsDown(),
        isReady: getIsReady(),
    };
    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};


export const useAuth = () => useContext(AuthContext);

