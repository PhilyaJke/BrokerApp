package accelerator.group.brokerapp.Configuration;

import accelerator.group.brokerapp.Service.MVCService.AdditionalStocksInformationService.AdditionalStocksInformationMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.BrokeragePortfolioSecuritiesService.BrokeragePortfolioSecuritiesMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.BrokeragePortfolioService.BrokeragePortfolioMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.LastPriceOfSecurityService.LastPriceOfSecurityMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.SecuritiesService.SecuritiesMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.UserService.UserMVCServiceImpl;
import accelerator.group.brokerapp.WebSockets.WSHandler;
import accelerator.group.brokerapp.WebSockets.UserProfileHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final AdditionalStocksInformationMVCServiceImpl additionalStocksInformationMVCService;
    private final BrokeragePortfolioMVCServiceImpl brokeragePortfolioMVCService;
    private final LastPriceOfSecurityMVCServiceImpl lastPriceOfSecurityMVCService;
    private final SecuritiesMVCServiceImpl securitiesMVCService;
    private final UserMVCServiceImpl userMVCService;
    private final BrokeragePortfolioSecuritiesMVCServiceImpl brokeragePortfolioSecuritiesMVCService;

    @Autowired
    public WebSocketConfiguration(@Qualifier("AdditionalStocksInformationMVCService") AdditionalStocksInformationMVCServiceImpl additionalStocksInformationMVCService,
                                  @Qualifier("BrokeragePortfolioMVCService") BrokeragePortfolioMVCServiceImpl brokeragePortfolioMVCService,
                                  @Qualifier("LastPriceOfSecurityMVCService") LastPriceOfSecurityMVCServiceImpl lastPriceOfSecurityMVCService,
                                  @Qualifier("SecuritiesMVCService") SecuritiesMVCServiceImpl securitiesMVCService,
                                  @Qualifier("UserMVCService") UserMVCServiceImpl userMVCService,
                                  @Qualifier("BrokeragePortfolioSecuritiesMVCService") BrokeragePortfolioSecuritiesMVCServiceImpl brokeragePortfolioSecuritiesMVCService){
        this.additionalStocksInformationMVCService = additionalStocksInformationMVCService;
        this.brokeragePortfolioMVCService = brokeragePortfolioMVCService;
        this.lastPriceOfSecurityMVCService = lastPriceOfSecurityMVCService;
        this.securitiesMVCService = securitiesMVCService;
        this.userMVCService = userMVCService;
        this.brokeragePortfolioSecuritiesMVCService = brokeragePortfolioSecuritiesMVCService;
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
        return new WSHandler(
                lastPriceOfSecurityMVCService,
                securitiesMVCService,
                additionalStocksInformationMVCService);
    }

    @Bean
    public UserProfileHandler userHandler(){
        return new UserProfileHandler(
                additionalStocksInformationMVCService,
                brokeragePortfolioMVCService,
                lastPriceOfSecurityMVCService,
                securitiesMVCService,
                userMVCService,
                brokeragePortfolioSecuritiesMVCService);
    }
}
