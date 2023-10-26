package accelerator.group.brokerapp.Service.DAOService.SecuritiesService;

import accelerator.group.brokerapp.Entity.*;
import accelerator.group.brokerapp.Responses.SecuritiesPageResponse;
import org.springframework.data.domain.Pageable;
import ru.tinkoff.piapi.core.InvestApi;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SecuritiesDAOService {

    SecuritiesPageResponse findAllSecuritiesPage(Pageable pageable);

    SecuritiesPageResponse findAllRuSecuritiesPage(Pageable pageable);

    SecuritiesPageResponse findAllForeignSecuritiesPage(Pageable pageable);

    List<Securities> findSecuritiesByRequest(String request);

    Securities findSecurityByTicker(String ticker);
    List<Securities> findAllSecurities();

}
