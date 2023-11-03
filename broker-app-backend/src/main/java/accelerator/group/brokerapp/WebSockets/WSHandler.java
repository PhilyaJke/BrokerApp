package accelerator.group.brokerapp.WebSockets;

import accelerator.group.brokerapp.Entity.LastPriceOfSecurities;
import accelerator.group.brokerapp.Service.MVCService.AdditionalStocksInformationService.AdditionalStocksInformationMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.LastPriceOfSecurityService.LastPriceOfSecurityMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.SecuritiesService.SecuritiesMVCServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class WSHandler extends TextWebSocketHandler implements WebSocketHandler {

    private final Set<WebSocketSession> uriSet = new HashSet<>();
    private final LastPriceOfSecurityMVCServiceImpl lastPriceOfSecurityMVCService;
    private final SecuritiesMVCServiceImpl securitiesMVCService;
    private final AdditionalStocksInformationMVCServiceImpl additionalStocksInformationMVCService;

    @Autowired
    public WSHandler(@Qualifier("LastPriceOfSecurityMVCService") LastPriceOfSecurityMVCServiceImpl lastPriceOfSecurityMVCService,
                     @Qualifier("SecuritiesMVCService") SecuritiesMVCServiceImpl securitiesMVCService,
                     @Qualifier("AdditionalStocksInformationMVCService") AdditionalStocksInformationMVCServiceImpl additionalStocksInformationMVCService) {
        this.lastPriceOfSecurityMVCService = lastPriceOfSecurityMVCService;
        this.securitiesMVCService = securitiesMVCService;
        this.additionalStocksInformationMVCService = additionalStocksInformationMVCService;
    }

    /// Добавить таймер по истечению времени которого будет закрываться коннект
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        Double actualPrice = 0.0;

        try {

            String figi;

            log.info("Запрос на соединение с uri: {}", session.getUri());

            if(!uriSet.stream().anyMatch(x -> x.getUri().equals(session.getUri()))){
                uriSet.add(session);
            }else{
                log.info("Разрыв соединеия с предыдущим коннектом по uri: {}", session.getUri());
                uriSet.stream().filter(x -> x.getUri().equals(session.getUri())).findAny().get().close();
                uriSet.add(session);
                uriSet.remove(uriSet.stream().filter(x -> x.getUri().equals(session.getUri())).findAny().get());
            }
            figi = findFigiByTicker(session);

            try {
                while (session.isOpen()) {
                    Optional<LastPriceOfSecurities> optionalLastPriceOfSecurities = lastPriceOfSecurityMVCService.findById(figi);
                    if (optionalLastPriceOfSecurities.isPresent()) {
                        Double productAsString = optionalLastPriceOfSecurities.get().getPrice();
                        if (!productAsString.equals(actualPrice)) {
                            actualPrice = productAsString;
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.append("price", productAsString);
                            session.sendMessage(new TextMessage(jsonObject.toString()));
                        }
                    } else {
                        Double price = additionalStocksInformationMVCService.findAddStocksInfoById(securitiesMVCService.findSecurityByFigi(figi).getId()).getPrice();
                        if (!price.equals(actualPrice)) {
                            actualPrice = price;
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.append("price", price);
                            session.sendMessage(new TextMessage(jsonObject.toString()));
                        }
                    }
                }
            }catch (NullPointerException exc){
                log.info("Ошибка поиска акции в redis");
            }
        }catch (IOException | IllegalStateException exc){
            log.info("Разрыв соединения с uri: {}", session.getUri());
            uriSet.remove(session.getUri());
            session.close();
    }
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

    public String findFigiByTicker(WebSocketSession session){
        String ticker = Arrays.stream(Objects.requireNonNull(session.getUri()).getPath().split("/")).collect(Collectors.toList()).get(2);
        Optional<String> figi = securitiesMVCService.findFigiByTicker(ticker);
        return figi.get();
    }

}
