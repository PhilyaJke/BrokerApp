package accelerator.group.brokerapp.Configuration;

import accelerator.group.brokerapp.Repository.*;
import accelerator.group.brokerapp.WebSockets.WSHandler;
import accelerator.group.brokerapp.WebSockets.UserProfileHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final AdditionalStocksInformationRepository additionalStocksInformationRepository;
    private final BrokeragePortfolioRepository brokeragePortfolioRepository;
    private final LastPriceOfSecuritiesRepository lastPriceOfSecuritiesRepository;
    private final SecuritiesRepository securitiesRepository;
    private final UserRepository userRepository;
    private final BrokeragePortfolioSecuritiesRepository brokeragePortfolioSecuritiesRepository;

    @Autowired
    public WebSocketConfiguration(AdditionalStocksInformationRepository additionalStocksInformationRepository,
                                  BrokeragePortfolioRepository brokeragePortfolioRepository,
                                  LastPriceOfSecuritiesRepository lastPriceOfSecuritiesRepository,
                                  SecuritiesRepository securitiesRepository,
                                  UserRepository userRepository,
                                  BrokeragePortfolioSecuritiesRepository brokeragePortfolioSecuritiesRepository) {
        this.additionalStocksInformationRepository = additionalStocksInformationRepository;
        this.brokeragePortfolioRepository = brokeragePortfolioRepository;
        this.lastPriceOfSecuritiesRepository = lastPriceOfSecuritiesRepository;
        this.securitiesRepository = securitiesRepository;
        this.userRepository = userRepository;
        this.brokeragePortfolioSecuritiesRepository = brokeragePortfolioSecuritiesRepository;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myHandler(), "/price", "/price/{ticker}")
                .addHandler(userHandler(), "/profile", "/profile/{username}")
                .setAllowedOriginPatterns("*")
//                .withSockJS()
        ;
    }

    @Bean
    public WSHandler myHandler() {
        return new WSHandler(lastPriceOfSecuritiesRepository, securitiesRepository, additionalStocksInformationRepository);
    }

    @Bean
    public UserProfileHandler userHandler(){
        return new UserProfileHandler(lastPriceOfSecuritiesRepository, securitiesRepository, brokeragePortfolioRepository, userRepository, additionalStocksInformationRepository, brokeragePortfolioSecuritiesRepository);
    }
}
