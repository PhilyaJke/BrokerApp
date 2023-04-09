package accelerator.group.brokerapp.Configuration;

import accelerator.group.brokerapp.Repository.LastPriceOfSecuritiesRepository;
import accelerator.group.brokerapp.Repository.SecuritiesRepository;
import accelerator.group.brokerapp.WebSockets.WSHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final LastPriceOfSecuritiesRepository lastPriceOfSecuritiesRepository;
    private final SecuritiesRepository securitiesRepository;

    @Autowired
    public WebSocketConfiguration(LastPriceOfSecuritiesRepository lastPriceOfSecuritiesRepository, SecuritiesRepository securitiesRepository) {
        this.lastPriceOfSecuritiesRepository = lastPriceOfSecuritiesRepository;
        this.securitiesRepository = securitiesRepository;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myHandler(), "/price", "/price/{ticker}")
                .setAllowedOriginPatterns("*")
//                .withSockJS()
        ;
    }

    @Bean
    public WSHandler myHandler() {
        return new WSHandler(lastPriceOfSecuritiesRepository, securitiesRepository);
    }
}
