package accelerator.group.brokerapp.Rest;

import accelerator.group.brokerapp.Repository.SecuritiesRepository;
import accelerator.group.brokerapp.Service.SecuritiesService.SecuritiesServiceImpl;
import com.google.gson.Gson;
import com.owlike.genson.Genson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.contract.v1.Quotation;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class SecuritiesRestApi {

    private final SecuritiesServiceImpl securitiesService;
    private final SecuritiesRepository securitiesRepository;

    @Autowired
    public SecuritiesRestApi(SecuritiesServiceImpl securitiesService, SecuritiesRepository securitiesRepository) {
        this.securitiesService = securitiesService;
        this.securitiesRepository = securitiesRepository;
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

    //ПЕРЕПИСАТЬ СРОЧНО
    @GetMapping("/api/securities/list/stock")
    public ResponseEntity findFullInfo(@RequestParam String ticker){
        System.out.println(ticker);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        var s = securitiesService.getSecuritiesInfoFromApi(securitiesRepository.findByTicker(ticker).get().getFigi());
        List<Map<String, String>> list = new ArrayList<>();
        for(int i = 0; i < s.size(); i++){
            Map<String, String> map = new HashMap();
            map.put("high", String.valueOf(Double.valueOf(String.valueOf(s.get(i).getHigh().getUnits()).concat(".").concat(String.valueOf(s.get(i).getHigh().getNano())))));
            map.put("low", String.valueOf(Double.valueOf(String.valueOf(s.get(i).getLow().getUnits()).concat(".").concat(String.valueOf(s.get(i).getLow().getNano())))));
            map.put("close", String.valueOf(Double.valueOf(String.valueOf(s.get(i).getClose().getUnits()).concat(".").concat(String.valueOf(s.get(i).getClose().getNano())))));
            map.put("open", String.valueOf(Double.valueOf(String.valueOf(s.get(i).getOpen().getUnits()).concat(".").concat(String.valueOf(s.get(i).getOpen().getNano())))));
            map.put("date", simpleDateFormat.format(Date.from(Instant.ofEpochSecond(s.get(i).getTime().getSeconds()))));
            list.add(map);
        }
        return ResponseEntity.ok(list);
    }

    public String decodeJson(String json, String key){
        Genson genson = new Genson();
        Map<String, String> jsonMap = genson.deserialize(json, Map.class);
        return jsonMap.get(key);
    }
}


//[, other, green_buildings, ecomaterials, financial, it, utilities, industrials, materials, health_care, green_energy, telecom, electrocars, consumer, energy, real_estate]
//        [, DE, RU, BE, HK, FI, TW, JP, LU, BM, FR, BR, SE, SG, GB, IE, US, CA, IL, UY, IN, CH, KR, CN, IT, KZ, AR, AU, PE, NL]