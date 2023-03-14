package accelerator.group.brokerapp.Rest;

import accelerator.group.brokerapp.Entity.Securities;
import accelerator.group.brokerapp.Responses.SecuritiesResponses;
import accelerator.group.brokerapp.Service.SecuritiesService.SecuritiesServiceImpl;
import com.owlike.genson.Genson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class SecuritiesRestApi {

    private final SecuritiesServiceImpl securitiesService;

    @Autowired
    public SecuritiesRestApi(SecuritiesServiceImpl securitiesService) {
        this.securitiesService = securitiesService;
    }

    @GetMapping("/api/securities/list/allforeignsecurities")
    public ResponseEntity findAllForeignSecurities(){
        if(!securitiesService.findAllForeignSecurities().isEmpty()) {
            return ResponseEntity.ok(securitiesService.findAllForeignSecurities());
        }else{
            return new ResponseEntity("Foreigns securities not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/api/securities/list/allrusecurities")
    public ResponseEntity findAllRuSecurities(){
        if(!securitiesService.findAllRuSecurities().isEmpty()) {
            return ResponseEntity.ok(securitiesService.findAllRuSecurities());
        }else{
            return new ResponseEntity("Ru securities not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/api/securities/list/allsecurities")
    public ResponseEntity findAllSecurities(){
        if(!securitiesService.findAllSecurities().isEmpty()) {
            return ResponseEntity.ok(securitiesService.findAllSecurities());
        }else{
            return new ResponseEntity("All securities not found", HttpStatus.NOT_FOUND);
        }
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
