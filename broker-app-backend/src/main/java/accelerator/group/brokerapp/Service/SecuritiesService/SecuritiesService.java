package accelerator.group.brokerapp.Service.SecuritiesService;

import accelerator.group.brokerapp.Entity.Securities;

import java.util.List;

public interface SecuritiesService {

    List<Securities> findAllSecurities();

    List<Securities> findAllForeignSecurities();

    List<Securities> findAllRuSecurities();

    List<Securities> findForeignSecurities(String county);

    List<Securities> findSecuritiesBySector(String sector);


}
