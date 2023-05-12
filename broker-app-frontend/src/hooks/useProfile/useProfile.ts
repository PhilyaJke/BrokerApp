import {useState} from "react";
import {getProfile} from "./api";
import {IProfile} from "./useProfile.model";
import {useTokens} from "../../providers/authProvider/authProvider";

const useProfile = () => {
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');
    const {accessToken} = useTokens();
    const handleMyProfile = async (): Promise<IProfile | null> => {
        try {
            setIsLoading(true);
            const response = await getProfile(accessToken);
            setIsLoading(false)
            return response;
        } catch (e) {
            setIsLoading(false)
            setError(e as string);
            return null;
        }
    }

    return {
        isLoading,
        error,
        handleMyProfile
    }
}

export default useProfile;