package accelerator.group.brokerapp.Service.DAOService.LastPriceOfSecurityService;

import accelerator.group.brokerapp.Entity.LastPriceOfSecurities;
import accelerator.group.brokerapp.Repository.LastPriceOfSecuritiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
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
}
