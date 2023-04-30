package accelerator.group.brokerapp.Rest;

import accelerator.group.brokerapp.Entity.BrokeragePortfolioSecurities;
import accelerator.group.brokerapp.Entity.User;
import accelerator.group.brokerapp.Repository.*;
import accelerator.group.brokerapp.Requests.BuySecurityRequest;
import accelerator.group.brokerapp.Security.JwtTokenProvider;
import accelerator.group.brokerapp.Service.SecuritiesService.SecuritiesServiceImpl;
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

    private final SecuritiesServiceImpl securitiesService;
    private final SecuritiesRepository securitiesRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final LastPriceOfSecuritiesRepository lastPriceOfSecuritiesRepository;
    private final BrokeragePortfolioRepository brokeragePortfolioRepository;
    private final BrokeragePortfolioSecuritiesRepository brokeragePortfolioSecuritiesRepository;
    private final AdditionalStocksInformationRepository additionalStocksInformationRepository;

    @Autowired
    public SecuritiesRestApi(SecuritiesServiceImpl securitiesService,
                             SecuritiesRepository securitiesRepository,
                             JwtTokenProvider jwtTokenProvider,
                             UserRepository userRepository,
                             LastPriceOfSecuritiesRepository lastPriceOfSecuritiesRepository,
                             BrokeragePortfolioRepository brokeragePortfolioRepository,
                             BrokeragePortfolioSecuritiesRepository brokeragePortfolioSecuritiesRepository,
                             AdditionalStocksInformationRepository additionalStocksInformationRepository) {
        this.securitiesService = securitiesService;
        this.securitiesRepository = securitiesRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.lastPriceOfSecuritiesRepository = lastPriceOfSecuritiesRepository;
        this.brokeragePortfolioRepository = brokeragePortfolioRepository;
        this.brokeragePortfolioSecuritiesRepository = brokeragePortfolioSecuritiesRepository;
        this.additionalStocksInformationRepository = additionalStocksInformationRepository;
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
        var securities = securitiesService.findSecuritiesByRequest(request);
        return securities.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(securities);
    }

    @GetMapping("/api/securities/list/stock")
    public ResponseEntity findFullInfo(@RequestParam String ticker){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        var security = securitiesRepository.findByTicker(ticker).get();
        var s = securitiesService.getSecuritiesInfoFromApi(security.getFigi());
        JSONObject jsonObject = new JSONObject();
        List<Map<String, Object>> list = new ArrayList<>();
        for (ru.tinkoff.piapi.contract.v1.HistoricCandle historicCandle : s) {
            parseToDoublePrice(historicCandle.getHigh().getUnits(), historicCandle.getHigh().getNano());
            Map<String, Object> map = new HashMap();
            map.put("high", parseToDoublePrice(historicCandle.getHigh().getUnits(), historicCandle.getHigh().getNano()));
            map.put("low", parseToDoublePrice(historicCandle.getLow().getUnits(), historicCandle.getLow().getNano()));
            map.put("close", parseToDoublePrice(historicCandle.getClose().getUnits(), historicCandle.getClose().getNano()));
            map.put("open", parseToDoublePrice(historicCandle.getOpen().getUnits(), historicCandle.getOpen().getNano()));
            map.put("date", simpleDateFormat.format(Date.from(Instant.ofEpochSecond(historicCandle.getTime().getSeconds()))));
            list.add(map);
        }
        jsonObject.put("candles", list);
        jsonObject.put("lot", additionalStocksInformationRepository.findAddStocksInfoById(security.getId()).getLot());
        return ResponseEntity.ok(jsonObject.toMap());
    }

    @PostMapping("/api/buySecurity")
    public ResponseEntity buySecurity(@RequestBody BuySecurityRequest buySecurityRequest,
                                      @RequestHeader(value = "Authorization") String Authorization){
        log.info("Покупка акций - {}", buySecurityRequest.getTicker());
        Optional<User> user = userRepository.findByUsername(jwtTokenProvider.getUsername(Authorization));
        if(user.isPresent()){
            var securities = securitiesRepository.findByTicker(buySecurityRequest.getTicker()).get();
            var portfolio = brokeragePortfolioSecuritiesRepository.findPortfolioByUserIdAndSecurityId(user.get().getId(), securities.getId());
            if(portfolio != null) {
                var subposrtfolio = brokeragePortfolioSecuritiesRepository.findById(portfolio.getId()).get();
                subposrtfolio.setCount(
                        subposrtfolio.getCount()+buySecurityRequest.getValue()*additionalStocksInformationRepository.findAddStocksInfoById(securities.getId()).getLot()
                );
                brokeragePortfolioSecuritiesRepository.save(subposrtfolio);
            }else{
                var brokeragePortfolio = brokeragePortfolioRepository.findPortfolioByUserId(user.get().getId());
                BrokeragePortfolioSecurities brokeragePortfolioSecurities = new BrokeragePortfolioSecurities(
                        securitiesRepository.findByTicker(buySecurityRequest.getTicker()).get(),
                        brokeragePortfolio,
                        buySecurityRequest.getValue()*additionalStocksInformationRepository.findAddStocksInfoById(securities.getId()).getLot()
                );
                brokeragePortfolioSecuritiesRepository.save(brokeragePortfolioSecurities);
            }
            return ResponseEntity.ok(buildJson(buySecurityRequest).toMap());
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
            var securities = securitiesRepository.findByTicker(buySecurityRequest.getTicker()).get();
            var portfolio = brokeragePortfolioSecuritiesRepository.findPortfolioByUserIdAndSecurityId(user.get().getId(), securities.getId());
            var subposrtfolio = brokeragePortfolioSecuritiesRepository.findById(portfolio.getId()).get();
            if(subposrtfolio!=null && subposrtfolio.getCount() > buySecurityRequest.getValue()){
                subposrtfolio.setCount(
                        subposrtfolio.getCount()-buySecurityRequest.getValue()*additionalStocksInformationRepository.findAddStocksInfoById(securities.getId()).getLot()
                );
                brokeragePortfolioSecuritiesRepository.save(subposrtfolio);
            }else if(portfolio!=null && subposrtfolio.getCount() == buySecurityRequest.getValue()){
                brokeragePortfolioSecuritiesRepository.deleteById(subposrtfolio.getId());
            }
            return ResponseEntity.ok(buildJson(buySecurityRequest).toMap());
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

    public JSONObject buildJson(BuySecurityRequest buySecurityRequest){
        JSONObject jsonObject = new JSONObject();
        jsonObject.append("count", buySecurityRequest.getValue());
        if(lastPriceOfSecuritiesRepository.findById(securitiesRepository.findByTicker(buySecurityRequest.getTicker()).get().getFigi()).isPresent()) {
            jsonObject.append("price", lastPriceOfSecuritiesRepository.findById(securitiesRepository.findByTicker(buySecurityRequest.getTicker()).get().getFigi()).get().getPrice());
            jsonObject.append("sum", lastPriceOfSecuritiesRepository.findById(securitiesRepository.findByTicker(buySecurityRequest.getTicker()).get().getFigi()).get().getPrice() * buySecurityRequest.getValue());
        }else{
            jsonObject.append("price", additionalStocksInformationRepository.findAddStocksInfoById(securitiesRepository.findByTicker(buySecurityRequest.getTicker()).get().getId()).getPrice());
            jsonObject.append("sum", additionalStocksInformationRepository.findAddStocksInfoById(securitiesRepository.findByTicker(buySecurityRequest.getTicker()).get().getId()).getPrice() * buySecurityRequest.getValue());
        }
        return jsonObject;
    }
}


//[, other, green_buildings, ecomaterials, financial, it, utilities, industrials, materials, health_care, green_energy, telecom, electrocars, consumer, energy, real_estate]
//        [, DE, RU, BE, HK, FI, TW, JP, LU, BM, FR, BR, SE, SG, GB, IE, US, CA, IL, UY, IN, CH, KR, CN, IT, KZ, AR, AU, PE, NL]