import {AuthRes, LoginReq, RefreshAccessTokenRes, RegisterReq} from "./models";

import appConfig from "../../../config";

const API_URL = appConfig.URL;

export async function login(req: LoginReq): Promise<AuthRes> {
    const response = await fetch(`${API_URL}/api/auth/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(req),
    });

    if (!response.ok) {
        throw new Error('Failed to login');
    }

    return response.json();
}

export async function register(req: RegisterReq): Promise<AuthRes> {
    console.log('register called');
    const response = await fetch(`${API_URL}/api/auth/registration`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(req),
    });

    if (!response.ok) {
        throw new Error('Failed to register');
    }

    return response.json();
}

export async function refreshAccessToken(refreshToken: string): Promise<RefreshAccessTokenRes> {
    console.log('refreshAccessToken called');
    const response = await fetch(`${API_URL}/api/auth/updateaccesstoken`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `${refreshToken}`
        }
    });

    if (!response.ok) {
        throw new Error('Failed to refresh access token');
    }

    return response.json();
}
