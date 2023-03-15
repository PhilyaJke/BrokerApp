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

    @GetMapping("/api/securities/list/allforeignsecurities")
    public ResponseEntity findAllForeignSecurities(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size){
        log.info("Пришел запрос на все иностранные акции");
//        if(!securitiesService.findAllForeignSecuritiesPage(pageable).) {
            return ResponseEntity.ok(securitiesService.findAllForeignSecuritiesPage(PageRequest.of(page, size)));
//        }else{
//            return new ResponseEntity("Foreigns securities not found", HttpStatus.NOT_FOUND);
//        }
    }

    @GetMapping("/api/securities/list/allrusecurities")
    public ResponseEntity findAllRuSecurities(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size){
//        if(!securitiesService.findAllRuSecuritiesPage(pageable).isEmpty()) {
            log.info("Пришел запрос на все российские акции");
            return ResponseEntity.ok(securitiesService.findAllRuSecuritiesPage(PageRequest.of(page, size)));
//        }else{
//            return new ResponseEntity("Ru securities not found", HttpStatus.NOT_FOUND);
//        }
    }

    @GetMapping("/api/securities/list/allsecurities")
    public ResponseEntity findAllSecurities(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size){
        log.info("Пришел запрос на все акции");
//        if(!securitiesService.findAllSecuritiesPage(pageable).isEmpty()) {
            return ResponseEntity.ok(securitiesService.findAllSecuritiesPage(PageRequest.of(page, size)));
//        }else{
//            return new ResponseEntity("All securities not found", HttpStatus.NOT_FOUND);
//        }
    }

    @GetMapping("/api/securities/list/specificforeignsecurities")
    public ResponseEntity findSpecificSecurities(@RequestHeader String country){
       // Добавить проверку что страна входит в список доступных стран
        if(!securitiesService.findForeignSecurities(country).isEmpty()){
            return ResponseEntity.ok(securitiesService.findForeignSecurities(country));
        }else{
            return new ResponseEntity("Specific security by country was not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/api/securities/list/specificsecuritiesbysector")
    public ResponseEntity findSecurityBySector(@RequestHeader String sector){
        System.out.println(sector);
        if(!securitiesService.findSecuritiesBySector(sector).isEmpty()){
            return ResponseEntity.ok(securitiesService.findSecuritiesBySector(sector));
        }else{
            return new ResponseEntity("Specific security by sector was not found", HttpStatus.NOT_FOUND);
        }
    }

}
