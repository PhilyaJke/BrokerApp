import AuthPage from "./pages/auth";
import {Navigate, Route, Routes} from "react-router-dom";
import RegisterPage from "./pages/register";
import {MainPage} from "./pages/main/index.";
import {useAuth, useIsAuthenticated} from "./providers/authProvider/authProvider";
import SecureApp from "./components/secureApp";
import AppLoader from "./components/appLoader";
import ServiceIsUnavailablePage from "./pages/serviceIsUnavailable";

const App = () => {
    // const {isReady, isDown} = useAuth();
    const {isDown, isLoading} = useAuth();
    const isAuth = useIsAuthenticated();
    if (isDown) {
        return <main>
            <ServiceIsUnavailablePage/>
        </main>
    }

    if (isLoading) {
        setTimeout(() => {
            console.log('isLoading', isLoading)
            if (isLoading) {
                console.log('isLoading true')
                return <App/>
            }
        }, 300);
        console.log('isLoading end', isLoading)
        return <AppLoader/>
    }
    if (isAuth) {
        return (
            <SecureApp/>
        )
    }
    return (
        <Routes>
            <Route path={"/"} element={<MainPage/>}/>
            <Route path={"/auth"} element={<AuthPage/>}/>
            <Route path={"/register"} element={<RegisterPage/>}/>
            <Route path={"*"} element={<Navigate to={"/"}/>}/>
            <Route path={'/ping'} element={<p>pong</p>}/>
            {/*<Route path={"*"} element={<h2>404.<br/>Page not exist :-(</h2>}/>*/}
        </Routes>
    )
}

export default App


