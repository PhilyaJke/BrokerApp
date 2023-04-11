package accelerator.group.brokerapp.Rest;

import accelerator.group.brokerapp.Entity.LastPriceOfSecurities;
import accelerator.group.brokerapp.Repository.LastPriceOfSecuritiesRepository;
import accelerator.group.brokerapp.Repository.SecuritiesRepository;
import accelerator.group.brokerapp.Service.SecuritiesService.SecuritiesServiceImpl;
import accelerator.group.brokerapp.TinkoffInvestApi.CacheableLastPrices;
import accelerator.group.brokerapp.WebSockets.WSHandler;
import com.owlike.genson.Genson;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.net.URI;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class SecuritiesRestApi {

    private final WSHandler WSHandler;
    private final SecuritiesServiceImpl securitiesService;
    private final SecuritiesRepository securitiesRepository;
    private final CacheableLastPrices cacheableLastPrices;
    private final LastPriceOfSecuritiesRepository lastPriceOfSecuritiesRepository;

    @Autowired
    public SecuritiesRestApi(WSHandler WSHandler, SecuritiesServiceImpl securitiesService, SecuritiesRepository securitiesRepository, CacheableLastPrices cacheableLastPrices, LastPriceOfSecuritiesRepository lastPriceOfSecuritiesRepository) {
        this.WSHandler = WSHandler;
        this.securitiesService = securitiesService;
        this.securitiesRepository = securitiesRepository;
        this.cacheableLastPrices = cacheableLastPrices;
        this.lastPriceOfSecuritiesRepository = lastPriceOfSecuritiesRepository;
    }

    @GetMapping("/api/securities/list/securities")
    public ResponseEntity showSecurities(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "ru") String region){
        switch (region){
            case "all" -> {
                log.info("Пришел запрос на все акции");
                return ResponseEntity.ok(securitiesService.findAllSecuritiesPage(PageRequest.of(page, size)));
            }
            case "foreign" ->{
                log.info("Пришел запрос на все иностранные акции");
                return ResponseEntity.ok(securitiesService.findAllForeignSecuritiesPage(PageRequest.of(page, size)));
            }
            default -> {
                log.info("Пришел запрос на все российские акции");
                return ResponseEntity.ok(securitiesService.findAllRuSecuritiesPage(PageRequest.of(page, size)));
            }
        }
    }

    @PostMapping("/api/securities/list/search")
    public ResponseEntity findSecuritiesInSearch(@RequestBody String search){
        log.info("поиск по акциям");
        String request = decodeJson(search, "search");
        var securities = securitiesRepository.findAll();
        var response = securities.stream().filter((s) -> s.getName().toLowerCase(Locale.ROOT).contains(request.toLowerCase(Locale.ROOT)) ||
                s.getTicker().toLowerCase(Locale.ROOT).contains(request.toLowerCase(Locale.ROOT))).collect(Collectors.toList()).stream().limit(5).collect(Collectors.toList());
        if(!response.isEmpty()){
            return ResponseEntity.ok(response);
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/securities/list/stock")
    public ResponseEntity findFullInfo(@RequestParam String ticker){
        System.out.println(ticker);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        var s = securitiesService.getSecuritiesInfoFromApi(securitiesRepository.findByTicker(ticker).get().getFigi());
        List<Map<String, Object>> list = new ArrayList<>();
        for(int i = 0; i < s.size(); i++){
            parseToDoublePrice(s.get(i).getHigh().getUnits(), s.get(i).getHigh().getNano() );
            Map<String, Object> map = new HashMap();
            map.put("high", parseToDoublePrice(s.get(i).getHigh().getUnits(), s.get(i).getHigh().getNano()));
            map.put("low", parseToDoublePrice(s.get(i).getLow().getUnits(), s.get(i).getLow().getNano()));
            map.put("close", parseToDoublePrice(s.get(i).getClose().getUnits(), s.get(i).getClose().getNano()));
            map.put("open", parseToDoublePrice(s.get(i).getOpen().getUnits(), s.get(i).getOpen().getNano()));
            map.put("date", simpleDateFormat.format(Date.from(Instant.ofEpochSecond(s.get(i).getTime().getSeconds()))));
            list.add(map);
        }
        return ResponseEntity.ok(list);
    }

    public Double parseToDoublePrice(long units, int nano) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(units).append(nano).insert(String.valueOf(units).length(), ".");
        return Double.valueOf(String.valueOf(stringBuilder));
    }

    public String decodeJson(String json, String key){
        Genson genson = new Genson();
        Map<String, String> jsonMap = genson.deserialize(json, Map.class);
        return jsonMap.get(key);
    }
}


//[, other, green_buildings, ecomaterials, financial, it, utilities, industrials, materials, health_care, green_energy, telecom, electrocars, consumer, energy, real_estate]
//        [, DE, RU, BE, HK, FI, TW, JP, LU, BM, FR, BR, SE, SG, GB, IE, US, CA, IL, UY, IN, CH, KR, CN, IT, KZ, AR, AU, PE, NL]