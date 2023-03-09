import React, {createContext, useContext, useEffect, useState} from "react";
import jwtDecode from "jwt-decode";
const config = {
    authUrl: 'http://localhost:1337/',
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
    isAuth: () => boolean,
}


const AuthContext = createContext<Context>({
    login: () => Promise.resolve({status: 'error', message: 'something went wrong'}),
    register: () => Promise.resolve({status: 'error', message: 'something went wrong'}),
    logout: () => Promise.resolve({status: 'error', message: 'something went wrong'}),
    getAccessToken: () => null,
    isAuth: () => false,
});


export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
    const [accessToken, setAccessToken] = useState<string | null>(null);
    const [refreshToken, setRefreshToken] = useState<string | null>(null);

    //on start get refresh token from localstorage
    useEffect(() => {
        const accessTokenFromLS = localStorage.getItem(config.refreshTokenLocalStorageKey);
        if (accessTokenFromLS) {
            refreshAccessToken(accessTokenFromLS)
                .then((accessTokenStatus: string) => {
                    if (accessTokenStatus === 'ok') {
                        setAccessToken(accessTokenFromLS);
                    } else {
                        setAccessToken(null);
                    }
                })
                .catch(() => {
                    setAccessToken(null);
                });
        }
    }, []);


    const refreshAccessToken = async (refreshToken: string): Promise<string> => {
        try {
            const response = await fetch(`${config.authUrl}/api/auth/updateaccesstoken`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ refreshToken }),
            });
            if (!response.ok) {
                return ('error');
            }
            const data: refreshAccessTokenRes = await response.json();
            if (!data || !data.accessToken) {
                return ('error');
            }
            setAccessToken(data.accessToken);
            return 'ok';
        } catch (error) {
            return ('error');
        }
    };




    //auto refresh access token every XX minutes if user is logged in
    useEffect(() => {
        const refreshTokenFromLS = localStorage.getItem(config.refreshTokenLocalStorageKey);

        if (!accessToken || !refreshTokenFromLS) {
            return;
        }

        const expirationTime = jwtDecode<{ exp: number }>(accessToken).exp * 1000;

        const refreshTime = expirationTime - Date.now() * 0.9; //рассчитываем время до обновления токена в мс (90% от времени жизни токена)

        const interval = setInterval(async () => {
            if (!accessToken || !refreshToken) {
                clearInterval(interval);
                return;
            }

            // if (Date.now() >= expirationTime) {
            //     setAccessToken(null);
            //     clearInterval(interval);
            //     return;
            // }

            try {
                const accessTokenStatus: string = await refreshAccessToken(refreshToken);

                if (accessTokenStatus === 'ok') {
                    const newAccessTokenExpirationTime = jwtDecode<{ exp: number }>(accessTokenStatus).exp * 1000;

                    if (newAccessTokenExpirationTime > expirationTime) {
                        localStorage.setItem(config.refreshTokenLocalStorageKey, refreshToken);
                        setAccessToken(accessTokenStatus);
                    }
                } else {
                    setAccessToken(null);
                    clearInterval(interval);
                }
            } catch (error) {
                setAccessToken(null);
                clearInterval(interval);
            }
        }, refreshTime);

        return () => clearInterval(interval);
    }, [accessToken, refreshToken]);



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
                return { status: 'error', message: 'Сервис временно недоступен' };
            }

            const data: AuthRes = await response.json();

            if (!data.refreshToken) {
                return { status: 'error', message: 'Неверный логин или пароль' };
            }

            setRefreshToken(data.refreshToken);

            return { status: 'ok', message: 'ok' };
        } catch (error) {
            console.error(`Failed to login: ${error}`);

            return { status: 'error', message: 'Сервис временно недоступен' };
        }
    }



    const logout = async () => {
        setAccessToken(null);
        setRefreshToken(null);
        localStorage.removeItem(config.refreshTokenLocalStorageKey);
        await fetch(config.authUrl + "/api/auth/logout", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({})
        })
    }

    const register = async (props: registerReq): Promise<contextRes> => {
        try {
            const response = await fetch(`${config.authUrl}/api/auth/register`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(props),
            });
            if (response.status === 500) {
                return { status: 'error', message: 'Сервис временно недоступен' };
            }
            const data: AuthRes = await response.json();
            if (!data.refreshToken) {
                return { status: 'error', message: 'Ошибка регистрации' };
            }
            setRefreshToken(data.refreshToken);
            return { status: 'ok', message: 'ok' };
        } catch (error) {
            console.error(`Failed to register: ${error}`);
            // @ts-ignore
            return { status: 'error', message: 'Сервис временно недоступен' };
        }
    };


        const getAccessToken = () => {
            return accessToken;
        }

        const isAuth = (): boolean => {
            return accessToken !== null;
        }

        const value: Context = {
            login,
            register,
            logout,
            getAccessToken,
            isAuth
        }

return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};



export const useAuth = () => useContext(AuthContext);

