import { List } from 'antd';
import { Link } from 'react-router-dom';
import Line from "antd/es/progress/Line";

export const MainPage = () => {
    const data = [
        { id: 1, name: 'Страница авторизации', link: '/auth' },
        { id: 2, name: 'Страница регистрации', link: '/register' },
        { id: 3, name: 'Профиль (защищеная страница)', link: '/profile' },
    ];

    return (
        <>
            <h2 style={{ color: 'white' }}>MainPage</h2>
            <List
                itemLayout="horizontal"
                dataSource={data}
                renderItem={(item) => (
                    <List.Item>
                        <List.Item.Meta
                            title={<Link to={item.link} style={{ color: 'white' }}>{item.name}</Link>}
                        />
                    </List.Item>
                )}
            />
        </>
    );
};
