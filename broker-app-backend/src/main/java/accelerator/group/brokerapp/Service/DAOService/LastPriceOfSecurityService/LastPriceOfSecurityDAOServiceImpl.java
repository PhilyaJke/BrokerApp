package accelerator.group.brokerapp.Service.DAOService.LastPriceOfSecurityService;

import accelerator.group.brokerapp.Entity.LastPriceOfSecurities;
import accelerator.group.brokerapp.Repository.LastPriceOfSecuritiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("LastPriceOfSecurityDAOService")
public class LastPriceOfSecurityDAOServiceImpl implements LastPriceOfSecurityDAOService{

    private final LastPriceOfSecuritiesRepository lastPriceOfSecuritiesRepository;

    @Autowired
    public LastPriceOfSecurityDAOServiceImpl(LastPriceOfSecuritiesRepository lastPriceOfSecuritiesRepository) {
        this.lastPriceOfSecuritiesRepository = lastPriceOfSecuritiesRepository;
    }

    @Override
    public Optional<LastPriceOfSecurities> findLastPriceOfSecurity(String figi) {
        return lastPriceOfSecuritiesRepository.findById(figi);
    }

    @Override
    public boolean existById(String id) {
        return lastPriceOfSecuritiesRepository.existsById(id);
    }

    @Override
    public void save(LastPriceOfSecurities lastPriceOfSecurities) {
        lastPriceOfSecuritiesRepository.save(lastPriceOfSecurities);
    }

    @Override
    public Optional<LastPriceOfSecurities> findById(String figi) {
        return lastPriceOfSecuritiesRepository.findById(figi);
    }

    @Override
    public void deleteById(String figi) {
        lastPriceOfSecuritiesRepository.deleteById(figi);
    }
}
