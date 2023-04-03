import {Navigate, Route, Routes} from "react-router-dom";
import {MainPage} from "./pages/main/index.";
import AuthPage from "./pages/auth";
import RegisterPage from "./pages/register";

const PublicApp = () => {
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

export default PublicApp;