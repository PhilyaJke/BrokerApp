import {useAuth} from "../../providers/authProvider";
import {Navigate} from "react-router-dom";
import {message} from "antd";

export const ProfilePage = () => {
    const {logout} = useAuth();

    const handleLogout = async () => {
        await logout();
        message.info("Вы вышли из аккаунта");
        return <Navigate to={"/"}/>;
    };

    return (
        <div>
            <h1>Профиль</h1>
            <button onClick={handleLogout}>Выйти</button>
        </div>
    );
};
