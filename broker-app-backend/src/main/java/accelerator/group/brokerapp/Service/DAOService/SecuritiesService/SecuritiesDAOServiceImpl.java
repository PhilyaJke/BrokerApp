package accelerator.group.brokerapp.Service.DAOService.SecuritiesService;

import accelerator.group.brokerapp.Entity.*;
import accelerator.group.brokerapp.Repository.*;
import accelerator.group.brokerapp.Responses.SecuritiesFullInfoResponse;
import accelerator.group.brokerapp.Responses.SecuritiesPageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("SecuritiesDAOService")
public class SecuritiesDAOServiceImpl implements SecuritiesDAOService {

    private final SecuritiesRepository securitiesRepository;

    @Autowired
    public SecuritiesDAOServiceImpl(SecuritiesRepository securitiesRepository) {
        this.securitiesRepository = securitiesRepository;
    }

    @Override
    public SecuritiesPageResponse findAllSecuritiesPage(Pageable pageable) {
        List<SecuritiesFullInfoResponse> securitiesPage = securitiesRepository.findAllSecuritiesPage(pageable);
        return new SecuritiesPageResponse(
                securitiesPage
        );
    }

    @Override
    public SecuritiesPageResponse findAllRuSecuritiesPage(Pageable pageable) {
        List<SecuritiesFullInfoResponse> securitiesPage = securitiesRepository.findAllRuSecuritiesPage(pageable);
        return new SecuritiesPageResponse(
                securitiesPage
        );
    }

    @Override
    public SecuritiesPageResponse findAllForeignSecuritiesPage(Pageable pageable) {
        List<SecuritiesFullInfoResponse> securitiesPage = securitiesRepository.findAllForeignSecuritiesPage(pageable);
        return new SecuritiesPageResponse(
                securitiesPage
        );
    }

    //TODO:переписать запрос на фильтровку сразу в поиске в бд
    @Override
    public List<Securities> findSecuritiesByRequest(String request){
        var securities = findAllSecurities();
        return securities.stream().filter((s) -> s.getName().toLowerCase(Locale.ROOT).contains(request.toLowerCase(Locale.ROOT)) ||
                s.getTicker().toLowerCase(Locale.ROOT).contains(request.toLowerCase(Locale.ROOT))).collect(Collectors.toList()).stream().limit(5).collect(Collectors.toList());
    }

    @Override
    public Securities findSecurityByTicker(String ticker) {
        return securitiesRepository.findSecuritiesByTicker(ticker).get();
    }

    @Override
    public List<Securities> findAllSecurities() {
        return securitiesRepository.findAll();
    }

    @Override
    public List<String> findLimitedSecurities(Pageable pageable) {
        return securitiesRepository.findLimitedSecurities(pageable);
    }

    @Override
    public List<String> findAllFigiSecurities() {
        return securitiesRepository.findAllFigiSecurities();
    }

    @Override
    public void save(Securities securities) {
        securitiesRepository.save(securities);
    }

    @Override
    public Securities findSecurityByFigi(String figi) {
        return securitiesRepository.findSecurityByFigi(figi);
    }

    @Override
    public Optional<String> findFigiByTicker(String ticker) {
        return securitiesRepository.findFigiByTicker(ticker);
    }

}
