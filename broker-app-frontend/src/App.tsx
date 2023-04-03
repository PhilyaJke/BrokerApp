import { useAuth, useIsAuthenticated } from "./providers/authProvider/authProvider";
import AppLoader from "./components/appLoader";
import PrivateApp from "./PrivateApp";
import PublicApp from "./PublicApp";
import ServiceIsUnavailablePage from "./pages/serviceIsUnavailable";

const App = () => {
    const {isDown, isLoading} = useAuth();
    const isAuth = useIsAuthenticated();

    // Если сервис недоступен, то отображаем страницу с сообщением об ошибке
    if (isDown) {
        return (
            <main>
                <ServiceIsUnavailablePage/>
            </main>
        );
    }

// Если идет загрузка, то отображаем лоадер
    if (isLoading) {
        // Задержка в 300 мс, чтобы избежать мерцания лоадера
        setTimeout(() => {
            if (isLoading) {
                return <PublicApp/>;
            }
        }, 300);
        return <AppLoader/>;
    }

// Если пользователь аутентифицирован, то отображаем приватное приложение
    if (isAuth) {
        return <PrivateApp/>;
    }

// В остальных случаях отображаем публичное приложение
    return <PublicApp/>;
}
export default App;