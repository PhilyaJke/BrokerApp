package accelerator.group.brokerapp.Service.SecuritiesService;

import accelerator.group.brokerapp.Entity.Securities;
import accelerator.group.brokerapp.Repository.SecuritiesRepository;
import accelerator.group.brokerapp.Responses.SecuritiesPageResponse;
import accelerator.group.brokerapp.Responses.StocksResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SecuritiesServiceImpl implements SecuritiesService{

    private final SecuritiesRepository securitiesRepository;

    @Autowired
    public SecuritiesServiceImpl(SecuritiesRepository securitiesRepository) {
        this.securitiesRepository = securitiesRepository;
    }

    @Override
    public SecuritiesPageResponse findAllSecuritiesPage(Pageable pageable) {
        Page<Securities> securitiesPage = securitiesRepository.findAllSecuritiesPage(pageable);
        return new SecuritiesPageResponse(
                securitiesPage.getContent(),
                securitiesPage.getNumber(),
                securitiesPage.getTotalPages()
        );
    }

    @Override
    public SecuritiesPageResponse findAllRuSecuritiesPage(Pageable pageable) {
        Page<Securities> securitiesPage = securitiesRepository.findAllRuSecuritiesPage(pageable);
        return new SecuritiesPageResponse(
                securitiesPage.getContent(),
                securitiesPage.getNumber(),
                securitiesPage.getTotalPages()
        );
    }

    @Override
    public SecuritiesPageResponse findAllForeignSecuritiesPage(Pageable pageable) {
        Page<Securities> securitiesPage = securitiesRepository.findAllForeignSecuritiesPage(pageable);
        return new SecuritiesPageResponse(
                securitiesPage.getContent(),
                securitiesPage.getNumber(),
                securitiesPage.getTotalPages()
        );
    }
}
