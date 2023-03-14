package accelerator.group.brokerapp.Service.SecuritiesService;

import accelerator.group.brokerapp.Entity.Securities;
import accelerator.group.brokerapp.Responses.SecuritiesResponses;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface SecuritiesService {

    List<Securities> findAllSecurities();

    List<Securities> findAllForeignSecurities();

    List<Securities> findAllRuSecurities();

    List<Securities> findForeignSecurities(String county);

    List<Securities> findSecuritiesBySector(String sector);


}
