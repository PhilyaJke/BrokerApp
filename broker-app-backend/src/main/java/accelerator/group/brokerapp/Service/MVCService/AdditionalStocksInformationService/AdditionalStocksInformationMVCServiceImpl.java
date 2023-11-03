package accelerator.group.brokerapp.Service.MVCService.AdditionalStocksInformationService;

import accelerator.group.brokerapp.Entity.AdditionalStocksInformation;
import accelerator.group.brokerapp.Repository.AdditionalStocksInformationRepository;
import accelerator.group.brokerapp.Requests.BuySecurityRequest;
import accelerator.group.brokerapp.Service.DAOService.AdditionalStocksInformationService.AdditionalStocksInformationDAOServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.SecuritiesService.SecuritiesMVCServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("AdditionalStocksInformationMVCService")
public class AdditionalStocksInformationMVCServiceImpl extends AdditionalStocksInformationDAOServiceImpl implements AdditionalStocksInformationMVCService {

    private final SecuritiesMVCServiceImpl securitiesMVCService;
    private final AdditionalStocksInformationDAOServiceImpl additionalStocksInformationDAOService;
    @Autowired
    public AdditionalStocksInformationMVCServiceImpl(AdditionalStocksInformationRepository additionalStocksInformationRepository,
                                                     @Qualifier("SecuritiesMVCService") SecuritiesMVCServiceImpl securitiesMVCService,
                                                     @Qualifier("AdditionalStocksInformationDAOService") AdditionalStocksInformationDAOServiceImpl additionalStocksInformationDAOService) {
        super(additionalStocksInformationRepository);
        this.securitiesMVCService = securitiesMVCService;
        this.additionalStocksInformationDAOService = additionalStocksInformationDAOService;
    }

    @Override
    public int getLotOfSecurity(String ticker) {
        var security = securitiesMVCService.findSecurityByTicker(ticker);
        return additionalStocksInformationDAOService.findAddStocksInfoById(security.getId()).getLot();
    }

    @Override
    public AdditionalStocksInformation checkAdditionalStockInfo(BuySecurityRequest buySecurityRequest) {
        return additionalStocksInformationDAOService.findAddStocksInfoById(securitiesMVCService.findSecurityByTicker(buySecurityRequest.getTicker()).getId());
    }
}
