import {Button, Card, Form, Input, message} from 'antd';
import {useFormik} from 'formik';
import * as Yup from 'yup';
import {useAuth} from '../../providers/authProvider';
import {Navigate, useNavigate} from "react-router-dom";
import {useState} from "react";

const initialValues = {
    username: '',
    password: '',
    email: '',
};

const validationSchema = Yup.object().shape({
    username: Yup.string()
        .min(3, 'Минимальная длина 3 символа')
        .max(20, 'Максимальная длина 20 символов')
        .required('Обязательное поле'),
    password: Yup.string()
        .min(3, 'Минимальная длина 3 символа')
        .max(20, 'Максимальная длина 20 символов')
        .required('Обязательное поле'),
    email: Yup.string().email('Неправильный формат email').required('Обязательное поле'),
});

const RegisterPage = () => {
    const {register, isAuth} = useAuth();
    const [error, setError] = useState('');
    const navigate = useNavigate();
    if (isAuth()) {
        return <Navigate to={'/profile'}/>;
    }
    const formik = useFormik({
        initialValues,
        validationSchema,
        onSubmit: async (values, {setSubmitting}) => {
            console.log('submit');
            try {
                const res = await register(values);
                console.log(res);
                if (res.status === 'ok') {
                    console.log(res);
                    return navigate('/profile');
                } else {
                    setError(res.message);
                    message.error(error);
                }
            } catch (error) {
                console.error(error);
                setError('Ошибка при регистрации');
                message.error('Ошибка при авторизации');
            }
            setSubmitting(false);
        },
    });

    return (
        <Card>
            <Form onFinish={formik.handleSubmit}>
                <h3>Регистрация</h3>
                {error && <code>{error}</code>}
                <Form.Item
                    label="Username"
                    name="username"
                    validateStatus={formik.errors.username ? 'error' : ''}
                    help={formik.errors.username ? formik.errors.username : null}
                >
                    <Input name="username" value={formik.values.username} onChange={formik.handleChange}/>
                </Form.Item>

                <Form.Item
                    label="Password"
                    name="password"
                    validateStatus={formik.errors.password ? 'error' : ''}
                    help={formik.errors.password ? formik.errors.password : null}
                >
                    <Input.Password name="password" value={formik.values.password} onChange={formik.handleChange}/>
                </Form.Item>

                <Form.Item
                    label="Email"
                    name="email"
                    validateStatus={formik.errors.email ? 'error' : ''}
                    help={formik.errors.email ? formik.errors.email : null}
                >
                    <Input name="email" value={formik.values.email} onChange={formik.handleChange}/>
                </Form.Item>

                <Button type="primary" htmlType="submit" disabled={formik.isSubmitting}>
                    Регистрация
                </Button>
            </Form>
        </Card>
    );
};

export default RegisterPage;

