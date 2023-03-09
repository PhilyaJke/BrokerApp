import AuthPage from "./pages/auth";
import {
    Route,
    Routes
} from "react-router-dom";
import RegisterPage from "./pages/register";
import {MainPage} from "./pages/main/index.";
import styled from "styled-components";
import {ProfilePage} from "./pages/profile";
const App = () =>
{
    return(
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
