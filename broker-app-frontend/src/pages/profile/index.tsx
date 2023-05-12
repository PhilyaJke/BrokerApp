import {useAuth} from "../../providers/authProvider/authProvider";
import {Navigate} from "react-router-dom";
import {List, message} from "antd";
import useProfile from "../../hooks/useProfile/useProfile";
import {IProfile} from "../../hooks/useProfile/useProfile.model";
import {useEffect, useState} from "react";
export const ProfilePage = () => {
    const [profile, setProfile] = useState<IProfile | null>(null);
    const {logout} = useAuth();
    const {handleMyProfile, isLoading, error} = useProfile();

    useEffect(() => {
        handleMyProfile().then(setProfile);
    }, []);

    const handleLogout = async () => {
        await logout();
        message.info("Вы вышли из аккаунта");
        return <Navigate to={"/"}/>;
    };

    if (isLoading) return <p>загрузка...</p>


    if (error) return <><h1>Прфиль недступен💀 💀 💀</h1><p>с бэка пришла ошибка<br/></p><code>см. вкладку network в панели разработчика</code></>
    console.log(error)
    return (
        <div>
            <h1>Профиль</h1>
            <p>Имя: {profile?.username}</p>
            <p>Бюджет: {profile?.budjet}</p>
            <List itemLayout="horizontal">
                {
                    profile?.securities.map((security) => (
                        <List.Item key={security.ticker}>
                            <List.Item.Meta
                                title={security.ticker}
                            />
                        </List.Item>
                    ))
                }
            </List>
            <button onClick={handleLogout}>Выйти</button>
        </div>
    );
};
