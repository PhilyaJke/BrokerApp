import {List, message} from 'antd';
import {Link} from 'react-router-dom';
import {useAuth, useIsAuthenticated} from "../../providers/authProvider/authProvider";

export const MainPage = () => {
    const data = [
        {id: 1, name: 'Страница авторизации', link: '/auth'},
        {id: 2, name: 'Страница регистрации', link: '/register'},
        {id: 3, name: 'Профиль (защищеная страница)', link: '/profile'},
    ];
    const {isDown} = useAuth();
    const isAuth: Boolean = useIsAuthenticated();
    if (isDown) {
        message.error('Бэкенд авторизации не работает☠️', 3);
    }
    // if (!isAuth) {
    //     message.info('Вы не авторизованы', 1);
    // }
    return (
        <>
            <h2 style={{color: 'white'}}>MainPage</h2>
            <List
                itemLayout="horizontal"
                dataSource={data}
                renderItem={(item) => (
                    <List.Item key={item.id}>
                        <List.Item.Meta
                            title={<Link to={item.link} style={{color: 'white'}}>{item.name}</Link>}
                        />
                    </List.Item>
                )}
            />
        </>
    );
};
