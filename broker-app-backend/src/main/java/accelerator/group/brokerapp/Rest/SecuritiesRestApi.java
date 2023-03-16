package accelerator.group.brokerapp.Rest;

import accelerator.group.brokerapp.Service.SecuritiesService.SecuritiesServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class SecuritiesRestApi {

    private final SecuritiesServiceImpl securitiesService;

    @Autowired
    public SecuritiesRestApi(SecuritiesServiceImpl securitiesService) {
        this.securitiesService = securitiesService;
    }

    @GetMapping("/api/securities/list/securities")
    public ResponseEntity findAllForeignSecurities(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "RU") String region){
        if(region.equals("all")) {
            log.info("Пришел запрос на все акции");
            return ResponseEntity.ok(securitiesService.findAllSecuritiesPage(PageRequest.of(page, size)));
        }else if(region.equals("foreign")){
            log.info("Пришел запрос на все иностранные акции");
            return ResponseEntity.ok(securitiesService.findAllForeignSecuritiesPage(PageRequest.of(page, size)));
        }else{
            log.info("Пришел запрос на все российские акции");
            return ResponseEntity.ok(securitiesService.findAllRuSecuritiesPage(PageRequest.of(page, size)));
        }
    }
}
