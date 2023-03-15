export interface LoginReq {
    email: string;
    password: string;
}

export interface RegisterReq {
    username: string;
    email: string;
    password: string;
}

export interface AuthRes {
    username: string;
    accessToken: string;
    refreshToken: string;
}

export interface RefreshAccessTokenRes {
    accessToken: string;
}

export default interface AuthTokens {
    accessToken: string | null;
    refreshToken: string | null;
}

export interface TokenContextValue {
    tokens: AuthTokens;
    setTokens: (tokens: AuthTokens) => void;
}



