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

    @Autowired
    public UserProfileHandler(LastPriceOfSecuritiesRepository lastPriceOfSecuritiesRepository,
                              SecuritiesRepository securitiesRepository,
                              BrokeragePortfolioRepository brokeragePortfolioRepository,
                              UserRepository userRepository,
                              AdditionalStocksInformationRepository additionalStocksInformationRepository) {
        this.lastPriceOfSecuritiesRepository = lastPriceOfSecuritiesRepository;
        this.securitiesRepository = securitiesRepository;
        this.brokeragePortfolioRepository = brokeragePortfolioRepository;
        this.userRepository = userRepository;
        this.additionalStocksInformationRepository = additionalStocksInformationRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Запрос на соединения с портфелем - {}", session.getUri().getPath());
        double price = 0.0;

        try {
            String username = findFigiByTicker(session);
            List<Securities> securities = brokeragePortfolioRepository.findSecuritiesByUser(userRepository.UserProfileInfo(username).getId());
            while (session.isOpen()) {
                JSONObject jsonPrices = new JSONObject();
                JSONObject jsonSumPrice = new JSONObject();
                double sum = 0;
                Map<String, JSONObject> map = new HashMap<>();
                for (int i = 0; i < securities.size(); i++) {
                    Optional<LastPriceOfSecurities> lastPriceOfSecurities = lastPriceOfSecuritiesRepository.findById(securities.get(i).getFigi());
                    var additionalStocksInformation = additionalStocksInformationRepository.findAddStocksInfoById(securities.get(i).getId());
                    if (lastPriceOfSecurities.isPresent()) {
                        Double prices = lastPriceOfSecurities.get().getPrice();
                        if (price != prices) {
                            price = prices;
                            sum += prices * additionalStocksInformation.getLot();
                            jsonPrices.put(securities.get(i).getTicker(), prices * additionalStocksInformation.getLot());
                            jsonSumPrice.put("budjet", sum);
                            map.put("securitiesPrices", jsonPrices);
                            map.put("summaryPrices", jsonPrices);
                            session.sendMessage(new TextMessage(map.toString()));
                        }
                    } else {
                        if (price != additionalStocksInformation.getPrice()) {
                            sum += additionalStocksInformation.getPrice() * additionalStocksInformation.getLot();
                            jsonPrices.put(securities.get(i).getTicker(), additionalStocksInformation.getPrice() * additionalStocksInformation.getLot());
                            jsonSumPrice.put("budjet", sum);
                            map.put("securitiesPrices", jsonPrices);
                            map.put("summaryPrices", jsonPrices);
                            session.sendMessage(new TextMessage(map.toString()));
                        }
                    }
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
