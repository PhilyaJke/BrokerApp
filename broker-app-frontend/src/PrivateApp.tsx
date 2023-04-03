import { Navigate, Route, Routes, useNavigate } from "react-router-dom";
import { Header } from "antd/es/layout/layout";
import { Menu, Popconfirm } from "antd";
import { useAuth } from "./providers/authProvider/authProvider";
import Overview from "./pages/overview";
import { ProfilePage } from "./pages/profile";
import SettingsPage from "./pages/settings/index.";
import Stock from "./pages/stock";

const PrivateApp = () => {
    const { logout } = useAuth();
    const navigator = useNavigate();

    const handleSignOut = () => {
        logout();
        return navigator("/auth");
    };

    // Массив с данными для меню
    const menuItems = [
        {
            label: <h1>Teenvest</h1>,
            key: "logo",
            onClick: () => navigator("/overview"),
            className: "logo",
        },
        {
            label: "Главная",
            key: "overview",
            onClick: () => navigator("/overview"),
            className: "menu-item",
        },
        {
            label: "Профиль",
            key: "profile",
            onClick: () => navigator("/profile"),
            className: "menu-item",
        },
        {
            label: "Настройки",
            key: "settings",
            onClick: () => navigator("/settings"),
            className: "menu-item",
        },
        {
            label: (
                <Popconfirm
                    title="Вы уверены, что хотите выйти?"
                    onConfirm={handleSignOut}
                    okText="Да"
                    cancelText="Нет"
                >
                    <a>Выйти</a>
                </Popconfirm>
            ),
            key: "logout",
            className: "menu-item",
        },
    ];

    // Массив с данными для роутов
    const routes = [
        { path: "/overview", element: <Overview /> },
        { path: "/profile", element: <ProfilePage /> },
        { path: "/settings", element: <SettingsPage /> },
        { path: "/quote/:ticker", element: <Stock /> },
    ];

    return (
        <>
            <Header style={{ width: "100%", paddingInline: "0" }}>
                <Menu theme="dark" mode="horizontal" selectedKeys={[]} items={menuItems} />
            </Header>
            <main>
                <Routes>
                    {/*Используем map для отрисовки роутов из массива*/}
                    {routes.map((route, index) => (
                        <Route path={route.path} element={route.element} key={index} />
                    ))}
                    {/*// Добавляем редирект на главную для несуществующих путей*/}
                    <Route path={"*"} element={<Navigate to={"/overview"} />} />
                </Routes>
            </main>
        </>
    );
};

export default PrivateApp;
