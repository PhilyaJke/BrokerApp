import AuthPage from "./pages/auth";
import {Navigate, Route, Routes} from "react-router-dom";
import RegisterPage from "./pages/register";
import {MainPage} from "./pages/main/index.";
import {useAuth} from "./providers/authProvider";
import SecureApp from "./components/secureApp";
import styled from "styled-components";
import AppLoader from "./components/appLoader";

const App = () => {
    const {isReady, isAuth, isDown} = useAuth();

    // if (isDown) {
    //     return <h2>Походу сервак упал☠️☠️☠️</h2>
    // }
    if (!isReady) {
        setTimeout(() => {
            if (isReady) {
                return <App/>
            }
        }, 300);
        return <AppLoader/>
    }
    if (isAuth) {
        return (
            <AppWrapper>
                <SecureApp/>
            </AppWrapper>
        )
    }
    return (
        <AppWrapper>
            <Routes>
                <Route path={"/"} element={<MainPage/>}/>
                <Route path={"/auth"} element={<AuthPage/>}/>
                <Route path={"/register"} element={<RegisterPage/>}/>
                <Route path={"*"} element={<Navigate to={"/"}/>}/>
                {/*<Route path={"*"} element={<h2>404.<br/>Page not exist :-(</h2>}/>*/}
            </Routes>
        </AppWrapper>
    )
}

export default App


const AppWrapper = styled.div`
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 0 auto;
`