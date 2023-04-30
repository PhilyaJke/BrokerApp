package accelerator.group.brokerapp.Service.SecuritiesService;

import accelerator.group.brokerapp.Entity.Securities;
import accelerator.group.brokerapp.Responses.SecuritiesPageResponse;
import org.springframework.data.domain.Pageable;
import ru.tinkoff.piapi.core.InvestApi;

import java.util.List;

public interface SecuritiesService {

    SecuritiesPageResponse findAllSecuritiesPage(Pageable pageable);

    SecuritiesPageResponse findAllRuSecuritiesPage(Pageable pageable);

    SecuritiesPageResponse findAllForeignSecuritiesPage(Pageable pageable);

    List<Securities> findSecuritiesByRequest(String request);

    InvestApi returnInvestApiConnection();

    List getSecuritiesInfoFromApi(String figi);
}
