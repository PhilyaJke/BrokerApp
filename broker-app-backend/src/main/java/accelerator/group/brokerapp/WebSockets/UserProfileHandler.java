package accelerator.group.brokerapp.WebSockets;

import accelerator.group.brokerapp.Entity.LastPriceOfSecurities;
import accelerator.group.brokerapp.Entity.Securities;
import accelerator.group.brokerapp.Repository.*;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final LastPriceOfSecuritiesRepository lastPriceOfSecuritiesRepository;
    private final SecuritiesRepository securitiesRepository;
    private final BrokeragePortfolioRepository brokeragePortfolioRepository;
    private final UserRepository userRepository;
    private final AdditionalStocksInformationRepository additionalStocksInformationRepository;
    private final BrokeragePortfolioSecuritiesRepository brokeragePortfolioSecuritiesRepository;

    @Autowired
    public UserProfileHandler(LastPriceOfSecuritiesRepository lastPriceOfSecuritiesRepository,
                              SecuritiesRepository securitiesRepository,
                              BrokeragePortfolioRepository brokeragePortfolioRepository,
                              UserRepository userRepository,
                              AdditionalStocksInformationRepository additionalStocksInformationRepository,
                              BrokeragePortfolioSecuritiesRepository brokeragePortfolioSecuritiesRepository) {
        this.lastPriceOfSecuritiesRepository = lastPriceOfSecuritiesRepository;
        this.securitiesRepository = securitiesRepository;
        this.brokeragePortfolioRepository = brokeragePortfolioRepository;
        this.userRepository = userRepository;
        this.additionalStocksInformationRepository = additionalStocksInformationRepository;
        this.brokeragePortfolioSecuritiesRepository = brokeragePortfolioSecuritiesRepository;
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
            List<Securities> securities = brokeragePortfolioRepository.findSecuritiesByUser(userRepository.UserProfileInfo(username).getId());
            while (session.isOpen()) {

                Double price;
                hashMap.clear();

                for (Securities security : securities) {

                    Optional<LastPriceOfSecurities> lastPriceOfSecurities = lastPriceOfSecuritiesRepository.findById(security.getFigi());
                    //TODO:Переписать на нормальный запрос к бд
                    var additionalStocksInformation = additionalStocksInformationRepository
                            .findAddStocksInfoById(securitiesRepository.findSecurityByFigi(security.getFigi()).getId());

                    if (lastPriceOfSecurities.isPresent()) {

                        Double prices = lastPriceOfSecurities.get().getPrice();
                        var portfolio = brokeragePortfolioSecuritiesRepository.findPortfolioByUserIdAndSecurityId(
                                userRepository.UserProfileInfo(username).getId(), securitiesRepository.findSecurityByFigi(security.getFigi()).getId());
                        price = brokeragePortfolioSecuritiesRepository.findById(portfolio.getId()).get().getCount() * prices * additionalStocksInformation.getLot();
                        if(!checkHashMap.containsKey(security.getTicker())) {
                            checkHashMap.put(security.getTicker(), price);
                        }
                        if (!checkHashMap.get(security.getTicker()).equals(price)) {
                            checkHashMap.remove(security.getTicker());
                            checkHashMap.put(security.getTicker(), price);
                            sum = checkHashMap.values().stream().flatMapToDouble(DoubleStream::of).sum();
                            hashMap.put(security.getTicker(), price);
                            jsonObject.append("securitiesPrices", hashMap);
                            jsonObject.append("summaryPrices", sum);
                            session.sendMessage(new TextMessage(jsonObject.toString()));
                            jsonObject.clear();
                            hashMap.clear();
                        }

                    } else {
                        var portfolio = brokeragePortfolioSecuritiesRepository.findPortfolioByUserIdAndSecurityId(
                                userRepository.UserProfileInfo(username).getId(), securitiesRepository.findSecurityByFigi(security.getFigi()).getId());
                        price = brokeragePortfolioSecuritiesRepository.findById(portfolio.getId()).get().getCount() * additionalStocksInformation.getPrice() * additionalStocksInformation.getLot();
                        if(!checkHashMap.containsKey(security.getTicker())) {
                            checkHashMap.put(security.getTicker(), price);
                        }
                        if (!checkHashMap.get(security.getTicker()).equals(price)) {
                            checkHashMap.remove(security.getTicker());
                            checkHashMap.put(security.getTicker(), price);
                            Double.sum(sum,price);
                            hashMap.put(security.getTicker(), price);
                            jsonObject.append("securitiesPrices", hashMap);
                            jsonObject.append("summaryPrices", sum);
                            session.sendMessage(new TextMessage(jsonObject.toString()));
                            jsonObject.clear();
                            hashMap.clear();
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

//    public sendMessage(Double actualPrice, Double price, double sum, Securities security, WebSocketSession session) throws IOException {
//        JSONObject jsonObject = new JSONObject();
//        Map<String, Object> map = new HashMap<>();
//        try {
//            if (!price.equals(actualPrice)) {
//                actualPrice = price;
//                Double.sum(sum,price);
//                map.put(security.getTicker(), price);
//                jsonObject.append("securitiesPrices", map);
//                jsonObject.append("summaryPrices", sum);
//                session.sendMessage(new TextMessage(jsonObject.toString()));
//            }
//        }catch (IOException exc){
//            log.info("Разрыв соединения с uri: {}", session.getUri());
//            session.close();
//        }
//        return jsonObject;
//    }


}
