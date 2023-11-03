package accelerator.group.brokerapp.Service.DAOService.SecuritiesService;

import accelerator.group.brokerapp.Entity.*;
import accelerator.group.brokerapp.Responses.SecuritiesPageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SecuritiesDAOService {

    SecuritiesPageResponse findAllSecuritiesPage(Pageable pageable);

    SecuritiesPageResponse findAllRuSecuritiesPage(Pageable pageable);

    SecuritiesPageResponse findAllForeignSecuritiesPage(Pageable pageable);

    List<Securities> findSecuritiesByRequest(String request);

    Securities findSecurityByTicker(String ticker);

    List<Securities> findAllSecurities();

    List<String> findLimitedSecurities(Pageable pageable);

    List<String> findAllFigiSecurities();

    void save(Securities securities);

    Securities findSecurityByFigi(String figi);

    Optional<String> findFigiByTicker(String ticker);

}
