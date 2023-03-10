import AuthPage from "./pages/auth";
import {Route, Routes} from "react-router-dom";
import RegisterPage from "./pages/register";
import {MainPage} from "./pages/main/index.";
import {ProfilePage} from "./pages/profile";
import {useAuth} from "./providers/authProvider";
import {Spin} from "antd";

const App = () => {
    const {isReady} = useAuth();
    //if not ready, show loading and check again after 300ms
    if (!isReady) {
        setTimeout(() => {
            if (isReady) {
                return <App/>
            }
        }, 300);
        return <Spin/>
    }
    return (
        <Routes>
            <Route path={"/"} element={<MainPage/>}/>
            <Route path={"/auth"} element={<AuthPage/>}/>
            <Route path={"/register"} element={<RegisterPage/>}/>
            <Route path={"/profile"} element={<ProfilePage/>}/>
            <Route path={"*"} element={<h2>404.<br/>Page not exist :-(</h2>}/>
        </Routes>
    )
}

export default App
