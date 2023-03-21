package accelerator.group.brokerapp.Service.SecuritiesService;

import accelerator.group.brokerapp.Entity.Securities;
import accelerator.group.brokerapp.Responses.SecuritiesPageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SecuritiesService {

    SecuritiesPageResponse findAllSecuritiesPage(Pageable pageable);

    SecuritiesPageResponse findAllRuSecuritiesPage(Pageable pageable);

    SecuritiesPageResponse findAllForeignSecuritiesPage(Pageable pageable);
}
