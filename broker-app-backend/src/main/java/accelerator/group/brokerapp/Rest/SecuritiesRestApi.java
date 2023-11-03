package accelerator.group.brokerapp.Rest;

import accelerator.group.brokerapp.Entity.User;
import accelerator.group.brokerapp.Requests.BuySecurityRequest;
import accelerator.group.brokerapp.Responses.SecuritiesPageResponse;
import accelerator.group.brokerapp.Security.JwtTokenProvider;
import accelerator.group.brokerapp.Service.MVCService.SecuritiesService.SecuritiesMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.UserService.UserMVCServiceImpl;
import com.owlike.genson.Genson;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class SecuritiesRestApi {

    private final SecuritiesMVCServiceImpl securitiesMVCService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMVCServiceImpl userMVCService;

    @Autowired
    public SecuritiesRestApi(@Qualifier("SecuritiesMVCService") SecuritiesMVCServiceImpl securitiesMVCService,
                             @Qualifier("UserMVCService") UserMVCServiceImpl userMVCService,
                             JwtTokenProvider jwtTokenProvider) {
        this.securitiesMVCService = securitiesMVCService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userMVCService = userMVCService;
    }

    @GetMapping("/api/securities/list/securities")
    public ResponseEntity showSecurities(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "ru") String region){
        SecuritiesPageResponse securitiesPageResponse = securitiesMVCService.showSecurities(page, size, region);
        return ResponseEntity.ok(securitiesPageResponse);
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
        JSONObject jsonObject = securitiesMVCService.findFullInfo(ticker);
        return ResponseEntity.ok(jsonObject);
    }

    @PostMapping("/api/buySecurity")
    public ResponseEntity buySecurity(@RequestBody BuySecurityRequest buySecurityRequest,
                                      @RequestHeader(value = "Authorization") String Authorization){
        log.info("Покупка акций - {}", buySecurityRequest.getTicker());
        User user = userMVCService.findByUsername(jwtTokenProvider.getUsername(Authorization))
                .orElseThrow(() -> new UsernameNotFoundException("User doesn't exist"));
        JSONObject jsonObject = securitiesMVCService.buySecurity(buySecurityRequest, user);
        return ResponseEntity.ok(jsonObject);
    }    

    @PostMapping("/api/sellSecurity")
    public ResponseEntity sellSecurity(@RequestBody BuySecurityRequest buySecurityRequest,
                                       @RequestHeader(value = "Authorization") String Authorization){
        log.info("Продажа акций - {}", buySecurityRequest.getTicker());
        User user = userMVCService.findByUsername(jwtTokenProvider.getUsername(Authorization))
                .orElseThrow(() -> new UsernameNotFoundException("User doesn't exist"));
        JSONObject jsonObject = securitiesMVCService.sellSecurity(buySecurityRequest, user);
        return ResponseEntity.ok(jsonObject);
    }

    public String decodeJson(String json, String key){
        Genson genson = new Genson();
        Map<String, String> jsonMap = genson.deserialize(json, Map.class);
        return jsonMap.get(key);
    }
}


//[, other, green_buildings, ecomaterials, financial, it, utilities, industrials, materials, health_care, green_energy, telecom, electrocars, consumer, energy, real_estate]
//        [, DE, RU, BE, HK, FI, TW, JP, LU, BM, FR, BR, SE, SG, GB, IE, US, CA, IL, UY, IN, CH, KR, CN, IT, KZ, AR, AU, PE, NL]