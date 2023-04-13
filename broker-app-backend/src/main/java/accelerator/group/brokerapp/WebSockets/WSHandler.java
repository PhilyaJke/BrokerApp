package accelerator.group.brokerapp.WebSockets;

import accelerator.group.brokerapp.Entity.LastPriceOfSecurities;
import accelerator.group.brokerapp.Repository.LastPriceOfSecuritiesRepository;
import accelerator.group.brokerapp.Repository.SecuritiesRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

//  @EventListener

@Slf4j
@Component
public class WSHandler extends TextWebSocketHandler implements WebSocketHandler {

    private Set<URI> uriSet = new HashSet<>();

    private final LastPriceOfSecuritiesRepository lastPriceOfSecuritiesRepository;
    private final SecuritiesRepository securitiesRepository;

    @Autowired
    public WSHandler(LastPriceOfSecuritiesRepository lastPriceOfSecuritiesRepository, SecuritiesRepository securitiesRepository) {
        this.lastPriceOfSecuritiesRepository = lastPriceOfSecuritiesRepository;
        this.securitiesRepository = securitiesRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        Double actualPrice = 0.0;

        try {

            String figi = "";

            log.info("Запрос на соединение с uri: {}", session.getUri());

            if (!uriSet.contains(session.getUri())) {
                uriSet.add(session.getUri());
                String ticker = Arrays.stream(session.getUri().getPath().split("/")).collect(Collectors.toList()).get(2);
                figi = securitiesRepository.findFigiByTicker(ticker).get();
            } else {
                session.close();
            }

            while (session.isOpen()) {
                Optional<LastPriceOfSecurities> optionalLastPriceOfSecurities = lastPriceOfSecuritiesRepository.findById(figi);
                if (optionalLastPriceOfSecurities.isPresent()) {
                    Double productAsString = optionalLastPriceOfSecurities.get().getPrice();
                    if(!productAsString.equals(actualPrice)){
                        actualPrice = productAsString;
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.append("price", productAsString);
                        session.sendMessage(new TextMessage(jsonObject.toString()));
                    }
                } else {
                    Double price = securitiesRepository.findSecurityByFigi(figi).getAdditionalStocksInformation().getPrice();
                    if(!price.equals(actualPrice)){
                        actualPrice = price;
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.append("price", price);
                        session.sendMessage(new TextMessage(jsonObject.toString()));
                    }
                }
            }
        }catch (IOException | IllegalStateException exc){
            log.info("Разрыв соединения с uri: {}", session.getUri());
            uriSet.remove(session.getUri());
            session.close();
    }
}



    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        try {
            uriSet.remove(session.getUri());
            session.close();
        }catch (IOException exc){
            log.info("Разрыв соединения с uri: {}", session.getUri());
        }
    }
}
