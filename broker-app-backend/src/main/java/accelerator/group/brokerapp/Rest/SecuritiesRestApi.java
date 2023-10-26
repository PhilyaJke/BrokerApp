package accelerator.group.brokerapp.Rest;

import accelerator.group.brokerapp.Entity.User;
import accelerator.group.brokerapp.Repository.*;
import accelerator.group.brokerapp.Requests.BuySecurityRequest;
import accelerator.group.brokerapp.Security.JwtTokenProvider;
import accelerator.group.brokerapp.Service.MVCService.AdditionalStocksInformationService.AdditionalStocksInformationMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.BrokeragePortfolioSecuritiesService.BrokeragePortfolioSecuritiesMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.BrokeragePortfolioService.BrokeragePortfolioMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.LastPriceOfSecurityService.LastPriceOfSecurityMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.SecuritiesService.SecuritiesMVCServiceImpl;
import com.owlike.genson.Genson;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class SecuritiesRestApi {

    private final SecuritiesMVCServiceImpl securitiesMVCService;
    private final BrokeragePortfolioSecuritiesMVCServiceImpl brokeragePortfolioSecuritiesMVCService;
    private final AdditionalStocksInformationMVCServiceImpl additionalStocksInformationMVCService;
    private final BrokeragePortfolioMVCServiceImpl brokeragePortfolioMVCService;
    private final LastPriceOfSecurityMVCServiceImpl lastPriceOfSecurityMVCService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Autowired
    public SecuritiesRestApi(SecuritiesMVCServiceImpl securitiesMVCService,
                             BrokeragePortfolioSecuritiesMVCServiceImpl brokeragePortfolioSecuritiesMVCService,
                             AdditionalStocksInformationMVCServiceImpl additionalStocksInformationMVCService,
                             BrokeragePortfolioMVCServiceImpl brokeragePortfolioMVCService,
                             LastPriceOfSecurityMVCServiceImpl lastPriceOfSecurityMVCService,
                             JwtTokenProvider jwtTokenProvider,
                             UserRepository userRepository) {
        this.securitiesMVCService = securitiesMVCService;
        this.brokeragePortfolioSecuritiesMVCService = brokeragePortfolioSecuritiesMVCService;
        this.additionalStocksInformationMVCService = additionalStocksInformationMVCService;
        this.brokeragePortfolioMVCService = brokeragePortfolioMVCService;
        this.lastPriceOfSecurityMVCService = lastPriceOfSecurityMVCService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @GetMapping("/api/securities/list/securities")
    public ResponseEntity showSecurities(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "ru") String region){
        switch (region){
            case "all" -> {
                log.info("Пришел запрос на все акции");
                return ResponseEntity.ok(securitiesMVCService.findAllSecuritiesPage(PageRequest.of(page, size)));
            }
            case "foreign" ->{
                log.info("Пришел запрос на все иностранные акции");
                return ResponseEntity.ok(securitiesMVCService.findAllForeignSecuritiesPage(PageRequest.of(page, size)));
            }
            default -> {
                log.info("Пришел запрос на все российские акции");
                return ResponseEntity.ok(securitiesMVCService.findAllRuSecuritiesPage(PageRequest.of(page, size)));
            }
        }
    }

    @PostMapping("/api/securities/list/search")
    public ResponseEntity findSecuritiesInSearch(@RequestBody String search){
        log.info("поиск по акциям");
        String request = decodeJson(search, "search");
        var securities = securitiesMVCService.findSecuritiesByRequest(request);
        return securities.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(securities);
    }

    @GetMapping("/api/securities/list/stock")
    public ResponseEntity findFullInfo(@RequestParam String ticker){
        log.info("Запрос на информацию об акции - {}", ticker);
//        Optional<User> user = userRepository.findByUsername(jwtTokenProvider.getUsername(Authorization));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        var securitiesCandles = securitiesMVCService.getSecuritiesInfoFromApiByTicker(ticker);
        JSONObject jsonObject = new JSONObject();
        List<Map<String, Object>> list = new ArrayList<>();
        for (ru.tinkoff.piapi.contract.v1.HistoricCandle historicCandle : securitiesCandles) {
            parseToDoublePrice(historicCandle.getHigh().getUnits(), historicCandle.getHigh().getNano());
            Map<String, Object> map = new HashMap();
            map.put("high", parseToDoublePrice(historicCandle.getHigh().getUnits(), historicCandle.getHigh().getNano()));
            map.put("low", parseToDoublePrice(historicCandle.getLow().getUnits(), historicCandle.getLow().getNano()));
            map.put("close", parseToDoublePrice(historicCandle.getClose().getUnits(), historicCandle.getClose().getNano()));
            map.put("open", parseToDoublePrice(historicCandle.getOpen().getUnits(), historicCandle.getOpen().getNano()));
            map.put("date", simpleDateFormat.format(Date.from(Instant.ofEpochSecond(historicCandle.getTime().getSeconds()))));
            list.add(map);
        }

//        var brokeragePortfolioSecurities = brokeragePortfolioSecuritiesRepository.findPortfolioByUserIdAndSecurityId(user.get().getId(), security.getId());
//        long count;
//        if(brokeragePortfolioSecurities == null){
//            count = 0;
//        }else{
//            count = brokeragePortfolioSecurities.getCount();
//        }
        jsonObject.put("candles", list);
        jsonObject.put("lot", additionalStocksInformationMVCService.getLotOfSecurity(ticker));
//        jsonObject.put("count", count);
        return ResponseEntity.ok(jsonObject.toMap());
    }

    @PostMapping("/api/buySecurity")
    public ResponseEntity buySecurity(@RequestBody BuySecurityRequest buySecurityRequest,
                                      @RequestHeader(value = "Authorization") String Authorization){
        log.info("Покупка акций - {}", buySecurityRequest.getTicker());
        Optional<User> user = userRepository.findByUsername(jwtTokenProvider.getUsername(Authorization));
        if(user.isPresent()){
            var portfolio = brokeragePortfolioSecuritiesMVCService.findPortfolioByUserIdAndTicker(user.get().getId() ,buySecurityRequest.getTicker());
            if(portfolio != null) {
                brokeragePortfolioSecuritiesMVCService.addPurchaseOfSecurityToPortfolio(portfolio.getId(), buySecurityRequest);
            }else{
                brokeragePortfolioSecuritiesMVCService.createPortfolioAndAddPurchaseOfSecurity(user.get().getId(), buySecurityRequest);
            }
            return ResponseEntity.ok(buildJson(buySecurityRequest));
        }else{
            return ResponseEntity.notFound().build();
        }
    }    

    @PostMapping("/api/sellSecurity")
    public ResponseEntity sellSecurity(@RequestBody BuySecurityRequest buySecurityRequest,
                                       @RequestHeader(value = "Authorization") String Authorization){
        log.info("Продажа акций - {}", buySecurityRequest.getTicker());
        Optional<User> user = userRepository.findByUsername(jwtTokenProvider.getUsername(Authorization));
        if(user.isPresent()) {
           brokeragePortfolioSecuritiesMVCService.sellSecurities(user.get().getId(), buySecurityRequest);
            return ResponseEntity.ok(buildJson(buySecurityRequest));
        }else{
            return ResponseEntity.notFound().build();
        }
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

    public HashMap buildJson(BuySecurityRequest buySecurityRequest){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("count", buySecurityRequest.getValue());
        if(lastPriceOfSecurityMVCService.checkLastPrice(buySecurityRequest).isPresent()){
            hashMap.put("price", lastPriceOfSecurityMVCService.checkLastPrice(buySecurityRequest).get().getPrice());
            hashMap.put("sum", lastPriceOfSecurityMVCService.checkLastPrice(buySecurityRequest).get().getPrice() * buySecurityRequest.getValue());
        }else{
            hashMap.put("price", additionalStocksInformationMVCService.checkAdditionalStockInfo(buySecurityRequest).getPrice());
            hashMap.put("sum", additionalStocksInformationMVCService.checkAdditionalStockInfo(buySecurityRequest).getPrice() * buySecurityRequest.getValue());
        }
        return hashMap;
    }
}


//[, other, green_buildings, ecomaterials, financial, it, utilities, industrials, materials, health_care, green_energy, telecom, electrocars, consumer, energy, real_estate]
//        [, DE, RU, BE, HK, FI, TW, JP, LU, BM, FR, BR, SE, SG, GB, IE, US, CA, IL, UY, IN, CH, KR, CN, IT, KZ, AR, AU, PE, NL]