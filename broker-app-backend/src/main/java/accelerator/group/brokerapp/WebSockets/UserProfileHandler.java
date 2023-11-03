package accelerator.group.brokerapp.WebSockets;

import accelerator.group.brokerapp.Entity.LastPriceOfSecurities;
import accelerator.group.brokerapp.Entity.Securities;
import accelerator.group.brokerapp.Service.MVCService.AdditionalStocksInformationService.AdditionalStocksInformationMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.BrokeragePortfolioSecuritiesService.BrokeragePortfolioSecuritiesMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.BrokeragePortfolioService.BrokeragePortfolioMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.LastPriceOfSecurityService.LastPriceOfSecurityMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.SecuritiesService.SecuritiesMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.UserService.UserMVCServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

@Slf4j
@Component
public class UserProfileHandler extends TextWebSocketHandler implements WebSocketHandler {

    private final LastPriceOfSecurityMVCServiceImpl lastPriceOfSecurityMVCService;
    private final SecuritiesMVCServiceImpl securitiesMVCService;
    private final BrokeragePortfolioMVCServiceImpl brokeragePortfolioMVCService;
    private final UserMVCServiceImpl userMVCService;
    private final AdditionalStocksInformationMVCServiceImpl additionalStocksInformationMVCService;
    private final BrokeragePortfolioSecuritiesMVCServiceImpl brokeragePortfolioSecuritiesMVCService;

    @Autowired
    public UserProfileHandler(@Qualifier("AdditionalStocksInformationMVCService") AdditionalStocksInformationMVCServiceImpl additionalStocksInformationMVCService,
                              @Qualifier("BrokeragePortfolioMVCService") BrokeragePortfolioMVCServiceImpl brokeragePortfolioMVCService,
                              @Qualifier("LastPriceOfSecurityMVCService") LastPriceOfSecurityMVCServiceImpl lastPriceOfSecurityMVCService,
                              @Qualifier("SecuritiesMVCService") SecuritiesMVCServiceImpl securitiesMVCService,
                              @Qualifier("UserMVCService") UserMVCServiceImpl userMVCService,
                              @Qualifier("BrokeragePortfolioSecuritiesMVCService") BrokeragePortfolioSecuritiesMVCServiceImpl brokeragePortfolioSecuritiesMVCService) {
        this.additionalStocksInformationMVCService = additionalStocksInformationMVCService;
        this.brokeragePortfolioMVCService = brokeragePortfolioMVCService;
        this.lastPriceOfSecurityMVCService = lastPriceOfSecurityMVCService;
        this.securitiesMVCService = securitiesMVCService;
        this.userMVCService = userMVCService;
        this.brokeragePortfolioSecuritiesMVCService = brokeragePortfolioSecuritiesMVCService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Запрос на соединения с портфелем - {}", Objects.requireNonNull(session.getUri()).getPath());

        JSONObject jsonObject = new JSONObject();
        HashMap<String, Double> checkHashMap = new HashMap<>();
        HashMap<String, Double> hashMap = new HashMap<>();
        Double sum = 0.0;

        try {
            String username = findFigiByTicker(session);
            List<Securities> securities = brokeragePortfolioMVCService.findUsersSecuritiesById(userMVCService.userProfileInfo(username).getId());
            while (session.isOpen()) {

                Double price;
                hashMap.clear();

                for (Securities security : securities) {

                    Optional<LastPriceOfSecurities> lastPriceOfSecurities = lastPriceOfSecurityMVCService.findById(security.getFigi());
                    //TODO:Переписать на нормальный запрос к бд
                    var additionalStocksInformation = additionalStocksInformationMVCService
                            .findAddStocksInfoById(securitiesMVCService.findSecurityByFigi(security.getFigi()).getId());

                    if (lastPriceOfSecurities.isPresent()) {

                        Double prices = lastPriceOfSecurities.get().getPrice();
                        var portfolio = brokeragePortfolioSecuritiesMVCService.findPortfolioByUserIdAndSecurityId(
                                userMVCService.userProfileInfo(username).getId(), securitiesMVCService.findSecurityByFigi(security.getFigi()).getId());
                        price = brokeragePortfolioSecuritiesMVCService.findBrokeragePortfolioSecuritiesByPortfolioId(portfolio.getId()).get().getCount() * prices;
                        if(!checkHashMap.containsKey(security.getTicker())) {
                            checkHashMap.put(security.getTicker(), price);
                        }
                        if (!checkHashMap.get(security.getTicker()).equals(price)) {
                            checkHashMap.remove(security.getTicker());
                            checkHashMap.put(security.getTicker(), price);
                            sum = checkHashMap.values().stream().flatMapToDouble(DoubleStream::of).sum();
                            jsonObject.append(security.getTicker(), price);
                            jsonObject.append("summaryPrices", sum);
                            session.sendMessage(new TextMessage(jsonObject.toString()));
                            jsonObject.clear();
                        }

                    } else {
                        var portfolio = brokeragePortfolioSecuritiesMVCService.findPortfolioByUserIdAndSecurityId(
                                userMVCService.userProfileInfo(username).getId(), securitiesMVCService.findSecurityByFigi(security.getFigi()).getId());
                        price = brokeragePortfolioSecuritiesMVCService.findBrokeragePortfolioSecuritiesByPortfolioId(portfolio.getId()).get().getCount() * additionalStocksInformation.getPrice();
                        if(!checkHashMap.containsKey(security.getTicker())) {
                            checkHashMap.put(security.getTicker(), price);
                            sum = checkHashMap.values().stream().flatMapToDouble(DoubleStream::of).sum();
                            jsonObject.append(security.getTicker(), price);
                            jsonObject.append("summaryPrices", sum);
                            session.sendMessage(new TextMessage(jsonObject.toString()));
                            jsonObject.clear();
                        }
                    }
                }
                Thread.sleep(5000);
            }
        }catch (IOException | IllegalStateException exc){
            log.info("Разрыв соединения с uri: {}", session.getUri());
            session.close();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        try {
            session.close();
        }catch (IOException exc){
            log.info("Разрыв соединения с uri: {}", session.getUri());
        }
    }

    public String findFigiByTicker(WebSocketSession session){
        String ticker = Arrays.stream(Objects.requireNonNull(session.getUri()).getPath().split("/")).collect(Collectors.toList()).get(2);
        return ticker;
    }
}
