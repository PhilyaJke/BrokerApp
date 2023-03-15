//JetBrains mono font import

const ServiceIsUnavailablePage = () => {
    return (
        <div style={{display: 'flex', flexDirection: 'column', gap: '10px'}}>
            <h1>☠️🖥️☠️</h1>
            <h1>Auth sервис недоступен</h1>
            <code>Не получилось обновить accessToken</code>
            <code>Проверь включен ли бэк и нет ли там ошибок</code>
            <code>URL AUTH-SERVICE: {}</code>
        </div>
    )
}

export default ServiceIsUnavailablePage