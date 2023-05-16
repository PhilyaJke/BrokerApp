import {useAuth} from "../../providers/authProvider/authProvider";
import {Navigate} from "react-router-dom";
import {Card, List, message} from "antd";
import useProfile from "../../hooks/useProfile/useProfile";
import {IProfile} from "../../hooks/useProfile/useProfile.model";
import {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
export const ProfilePage = () => {
    const navigate = useNavigate();
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
            <div style={{display: 'flex', justifyContent: 'center', alignItems: 'center', width: '100%'}}>
                {
                    profile?.securities.map((security) => (
                        <Card title={security.ticker} style={{width: 300}} onClick={() => navigate(`/quote/${security.ticker}`)}>
                            <img src={security.iconPath} style={{width: 50, height: 50}} alt={''}/>
                            <p><b>{security.name}</b></p>
                            <p>{security.region}</p>
                            <p>{security.sector}</p>
                            <p>{String(security.price)}$</p>
                        </Card>
                    ))
                }
            </div>
            <button onClick={handleLogout}>Выйти</button>
        </div>
    );
};
