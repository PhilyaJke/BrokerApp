import appConfig from "../../../config";
import {IProfile} from "./useProfile.model";

const API_URL = appConfig.URL;

export const getProfile = async (accessToken: string | null): Promise<IProfile> => {
    const response = await fetch(`${API_URL}/api/profile`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `${accessToken}`,
        },
    });
    if (!response.ok) {
        throw new Error('Failed to get profile');
    }
    const json = await response.json();
    return json as IProfile;
}