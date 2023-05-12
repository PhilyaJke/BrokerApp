import {useCallback, useEffect, useState} from "react";
import {getProfile} from "./api";
import {IProfile} from "./useProfile.model";
import {useTokens} from "../../providers/authProvider/authProvider";

const useProfile = () => {
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');
    const {accessToken} = useTokens();
    const [profile, setProfile] = useState<IProfile | null>();
    const handleMyProfile = async (): Promise<IProfile | null> => {
        try {
            setIsLoading(true);
            const response = await getProfile(accessToken);
            setIsLoading(false)
            setProfile(response);
            return response;
        } catch (e) {
            setIsLoading(false)
            setError(e as string);
            return null;
        }
    }

    useEffect(() => {
        handleMyProfile();
    }, [accessToken])

    const updateProfilePrices = useCallback(
        (updatedData: any) => {
            setProfile((prevProfile) => {
                if (!prevProfile) return null;
                const updatedSecurities = prevProfile.securities.map((sec) => {
                    if (updatedData.hasOwnProperty(sec.ticker)) {
                        return { ...sec, price: updatedData[sec.ticker][0] };
                    }
                    return sec;
                });
                return {
                    ...prevProfile,
                    summaryPrices: updatedData.summaryPrices[0],
                    securities: updatedSecurities,
                };
            });
        },
        [setProfile]
    );

    useEffect(() => {
        handleMyProfile();

        // Создаем подключение websocket
        const socket = new WebSocket(`ws://localhost:8080/profile/${profile?.username}`);

        // Слушаем события websocket
        socket.addEventListener("message", (event) => {
            try {
                const updatedData = JSON.parse(event.data);
                updateProfilePrices(updatedData);
            } catch (e) {
                console.error("Ошибка парсинга данных с WebSocket:", e);
            }
        });

        // Закрываем подключение, когда компонент размонтируется
        return () => socket.close();
    }, [accessToken, updateProfilePrices])

    return {
        isLoading,
        error,
        handleMyProfile
    }
}

export default useProfile;