package accelerator.group.brokerapp.Service.MVCService.LastPriceOfSecurityService;

import accelerator.group.brokerapp.Entity.LastPriceOfSecurities;
import accelerator.group.brokerapp.Repository.LastPriceOfSecuritiesRepository;
import accelerator.group.brokerapp.Requests.BuySecurityRequest;
import accelerator.group.brokerapp.Service.DAOService.LastPriceOfSecurityService.LastPriceOfSecurityDAOServiceImpl;
import accelerator.group.brokerapp.Service.DAOService.SecuritiesService.SecuritiesDAOServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LastPriceOfSecurityMVCServiceImpl extends LastPriceOfSecurityDAOServiceImpl implements LastPriceOfSecurityMVCService {
    private final LastPriceOfSecurityDAOServiceImpl lastPriceOfSecurityDAOService;
    private final SecuritiesDAOServiceImpl securitiesDAOService;
    @Autowired
    public LastPriceOfSecurityMVCServiceImpl(LastPriceOfSecuritiesRepository lastPriceOfSecuritiesRepository,
                                             LastPriceOfSecurityDAOServiceImpl lastPriceOfSecurityDAOService, SecuritiesDAOServiceImpl securitiesDAOService) {
        super(lastPriceOfSecuritiesRepository);
        this.lastPriceOfSecurityDAOService = lastPriceOfSecurityDAOService;
        this.securitiesDAOService = securitiesDAOService;
    }

    @Override
    public Optional<LastPriceOfSecurities> checkLastPrice(BuySecurityRequest buySecurityRequest) {
        return lastPriceOfSecurityDAOService.findLastPriceOfSecurity(securitiesDAOService.findSecurityByTicker(buySecurityRequest.getTicker()).getFigi());
    }

}
