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
        log.info("Запрос на соединения с портфелем - {}", session.getUri().getPath());

        JSONObject jsonObject = new JSONObject();

        try {
            String username = findFigiByTicker(session);
            List<Securities> securities = brokeragePortfolioRepository.findSecuritiesByUser(userRepository.UserProfileInfo(username).getId());
            while (session.isOpen()) {
                Double price;
                JSONObject jsonPrices = new JSONObject();
                JSONObject jsonSumPrice = new JSONObject();
                double sum = 0;
                Map<String, JSONObject> map = new HashMap<>();
                for (int i = 0; i < securities.size(); i++) {
                    Optional<LastPriceOfSecurities> lastPriceOfSecurities = lastPriceOfSecuritiesRepository.findById(securities.get(i).getFigi());
                    //Переписать на нормальный запрос к бд
                    var additionalStocksInformation = additionalStocksInformationRepository
                            .findAddStocksInfoById(securitiesRepository.findSecurityByFigi(securities.get(i).getFigi()).getId());
                    if (lastPriceOfSecurities.isPresent()) {
                        Double prices = lastPriceOfSecurities.get().getPrice();
                            var portfolio = brokeragePortfolioSecuritiesRepository.findPortfolioByUserIdAndSecurityId(
                                    userRepository.UserProfileInfo(username).getId(), securitiesRepository.findSecurityByFigi(securities.get(i).getFigi()).getId());
                            price = brokeragePortfolioSecuritiesRepository.findById(portfolio.getId()).get().getCount() * prices * additionalStocksInformation.getLot();;
                            sum += price;
                            jsonPrices.put(securities.get(i).getTicker(), price);
                    } else {
                            var portfolio = brokeragePortfolioSecuritiesRepository.findPortfolioByUserIdAndSecurityId(
                                    userRepository.UserProfileInfo(username).getId(), securitiesRepository.findSecurityByFigi(securities.get(i).getFigi()).getId());
                            price = brokeragePortfolioSecuritiesRepository.findById(portfolio.getId()).get().getCount() * additionalStocksInformation.getPrice() * additionalStocksInformation.getLot();;
                            sum += price;
                            jsonPrices.put(securities.get(i).getTicker(), price);
                    }
                }

                if(!jsonObject.similar(jsonPrices)){
                    jsonSumPrice.put("budjet", sum);
                    map.put("securitiesPrices", jsonPrices);
                    map.put("summaryPrices", jsonSumPrice);
                    session.sendMessage(new TextMessage(map.toString()));
                    jsonObject.clear();
                    jsonObject = jsonPrices;
                }

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
