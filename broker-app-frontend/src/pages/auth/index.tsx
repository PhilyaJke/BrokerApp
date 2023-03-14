import styled from "styled-components";
import {Button, Card, Form, Input, message} from 'antd';
import {useFormik} from "formik";
import * as Yup from "yup";
import {useAuth} from "../../providers/authProvider";
import {useNavigate} from "react-router-dom";
import {useState} from "react";

const validationSchema = Yup.object().shape({
    email: Yup.string()
        .email("Неправильный формат email")
        .required("Обязательное поле"),
    password: Yup.string()
        .min(3, "Минимальная длина 3 символа")
        .max(20, "Максимальная длина 20 символов")
        .required("Обязательное поле"),
});

const AuthPage = () => {
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const {isAuth, login} = useAuth();

    const formik = useFormik({
        initialValues: {
            email: '',
            password: '',
        },
        validationSchema,
        onSubmit: async (values, {setSubmitting}) => {
            console.log('submit');
            try {
                const res = await login(values);
                console.log(res);
                if (res.status === 'ok') {
                    console.log(res);
                    return navigate('/profile');
                } else {
                    message.error(res.message);
                }
            } catch (error) {
                console.error(error);
                message.error('Ошибка при авторизации');
            }
            setSubmitting(false);
        },
    });

    return (
        <Card>
            <Form
                layout="vertical"
                onFinish={formik.handleSubmit}
                style={{maxWidth: '400px', margin: '0 auto'}}
            >
                {error && <code>{error}</code>}
                <Form.Item
                    label="Почта"
                    required
                    validateStatus={formik.errors.email ? 'error' : ''}
                    help={formik.errors.email ? formik.errors.email : null}
                >
                    <Input
                        name="email"
                        value={formik.values.email}
                        onChange={formik.handleChange}
                        onBlur={formik.handleBlur}
                    />
                </Form.Item>
                <Form.Item
                    label="Пароль"
                    required
                    validateStatus={formik.errors.password ? 'error' : ''}
                    help={formik.errors.password ? formik.errors.password : null}
                >
                    <Input.Password
                        name="password"
                        value={formik.values.password}
                        onChange={formik.handleChange}
                        onBlur={formik.handleBlur}
                    />
                </Form.Item>

                <Form.Item>
                    <Button type="primary" htmlType="submit" disabled={formik.isSubmitting}>
                        Войти
                    </Button>
                </Form.Item>
            </Form>
        </Card>
    );
};

export default AuthPage


const AuthContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100vw;
`

const AuthForm = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  gap: 10px;
`
