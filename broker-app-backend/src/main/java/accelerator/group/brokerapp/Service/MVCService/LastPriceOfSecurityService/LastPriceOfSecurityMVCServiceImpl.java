package accelerator.group.brokerapp.Service.MVCService.LastPriceOfSecurityService;

import accelerator.group.brokerapp.Entity.LastPriceOfSecurities;
import accelerator.group.brokerapp.Repository.LastPriceOfSecuritiesRepository;
import accelerator.group.brokerapp.Requests.BuySecurityRequest;
import accelerator.group.brokerapp.Service.DAOService.LastPriceOfSecurityService.LastPriceOfSecurityDAOServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.SecuritiesService.SecuritiesMVCServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("LastPriceOfSecuritiesMVCService")
public class LastPriceOfSecurityMVCServiceImpl extends LastPriceOfSecurityDAOServiceImpl implements LastPriceOfSecurityMVCService {
    private final LastPriceOfSecurityDAOServiceImpl lastPriceOfSecurityDAOService;
    private final SecuritiesMVCServiceImpl securitiesMVCService;
    @Autowired
    public LastPriceOfSecurityMVCServiceImpl(LastPriceOfSecuritiesRepository lastPriceOfSecuritiesRepository,
                                             @Qualifier("LastPriceOfSecurityDAOService") LastPriceOfSecurityDAOServiceImpl lastPriceOfSecurityDAOService,
                                             @Qualifier("SecuritiesMVCService") SecuritiesMVCServiceImpl securitiesMVCService) {
        super(lastPriceOfSecuritiesRepository);
        this.lastPriceOfSecurityDAOService = lastPriceOfSecurityDAOService;
        this.securitiesMVCService = securitiesMVCService;
    }

    @Override
    public Optional<LastPriceOfSecurities> checkLastPrice(BuySecurityRequest buySecurityRequest) {
        return lastPriceOfSecurityDAOService.findLastPriceOfSecurity(securitiesMVCService.findSecurityByTicker(buySecurityRequest.getTicker()).getFigi());
    }

}
