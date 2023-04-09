package accelerator.group.brokerapp.WebSockets;

import accelerator.group.brokerapp.Entity.LastPriceOfSecurities;
import accelerator.group.brokerapp.Repository.LastPriceOfSecuritiesRepository;
import accelerator.group.brokerapp.Repository.SecuritiesRepository;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.core.MessagePostProcessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

//  @EventListener

@Component
public class WSHandler extends TextWebSocketHandler implements WebSocketHandler {

    private Map<WebSocketSession, URI> clients = new HashMap<>();

    private final LastPriceOfSecuritiesRepository lastPriceOfSecuritiesRepository;
    private final SecuritiesRepository securitiesRepository;

    @Autowired
    public WSHandler(LastPriceOfSecuritiesRepository lastPriceOfSecuritiesRepository, SecuritiesRepository securitiesRepository) {
        this.lastPriceOfSecuritiesRepository = lastPriceOfSecuritiesRepository;
        this.securitiesRepository = securitiesRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if(!clients.containsValue(session.getUri()) && clients.size() != 0){
            clients.put(session, session.getUri());
        }else{
            session.close();
        }
        while(true){
            for (int i = 0; i < clients.size(); i++) {
                var ticker = Arrays.stream(clients.get(i).getPath().split("/")).collect(Collectors.toList()).get(2);
                var figi = securitiesRepository.findFigiByTicker(ticker);
                if (figi.isPresent()) {
                    Optional<LastPriceOfSecurities> optionalLastPriceOfSecurities = lastPriceOfSecuritiesRepository.findById(figi.get());
                    if (optionalLastPriceOfSecurities.isPresent()) {
                        Double productAsString = optionalLastPriceOfSecurities.get().getPrice();
                        Timestamp timestamp = new java.sql.Timestamp(System.currentTimeMillis());
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.append("price", productAsString);
                        jsonObject.append("date", timestamp);
                        clients.keySet().stream().collect(Collectors.toList()).get(i).sendMessage(new TextMessage(jsonObject.toString()));
                    } else {
                        clients.keySet().stream().collect(Collectors.toList()).get(i).sendMessage(new TextMessage("хуй"));
                    }
                }
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
    }

    public Map<WebSocketSession, URI> getClients() {
        return clients;
    }

    public void setClients(Map<WebSocketSession, URI> clients) {
        this.clients = clients;
    }
}
