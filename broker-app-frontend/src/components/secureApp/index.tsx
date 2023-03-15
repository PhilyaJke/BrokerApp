import {Navigate, Route, Routes, useNavigate} from "react-router-dom";
import Overview from "../../pages/overview";
import {ProfilePage} from "../../pages/profile";
import {Header} from "antd/es/layout/layout";
import {Menu, Popconfirm} from "antd";
import {useAuth} from "../../providers/authProvider/authProvider";
import SettingsPage from "../../pages/settings/index.";

const SecureApp = () => {
    const {logout} = useAuth();
    const navigator = useNavigate();
    const handleSignOut = () => {
        logout();
        window.location.assign('/auth');
    }
    return (
        <>
            <Header style={{width: '100%'}}>
                <Menu theme="dark" mode="horizontal" selectedKeys={[]}>
                    <Menu.Item key="logo" onClick={() => navigator('/overview')}><h1>Broker App</h1></Menu.Item>
                    <Menu.Item key="overview" onClick={() => navigator('/overview')}>
                        Главная
                    </Menu.Item>
                    <Menu.Item key="profile" onClick={() => navigator('/profile')}>
                        Профиль
                    </Menu.Item>
                    <Menu.Item key="settings" onClick={() => navigator('/settings')}>
                        Настройки
                    </Menu.Item>
                    <Popconfirm title={"Точно?"} onConfirm={handleSignOut} okText={"Да"} cancelText={"Нет"}>
                        <Menu.Item key="sign-out">
                            Выйти
                        </Menu.Item>
                    </Popconfirm>
                </Menu>
            </Header>
            <main>
                <Routes>
                    <Route path={"/overview"} element={<Overview/>}/>
                    <Route path={'/profile'} element={<ProfilePage/>}/>
                    <Route path={'/settings'} element={<SettingsPage/>}/>
                    <Route path={"*"} element={<Navigate to={"/overview"}/>}/>
                </Routes>
            </main>
            <footer>
                <p>Broker App 2023</p>
            </footer>
        </>
    )
}

export default SecureApp;
