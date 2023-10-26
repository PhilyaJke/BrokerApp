package accelerator.group.brokerapp.Service.MVCService.AdditionalStocksInformationService;

import accelerator.group.brokerapp.Entity.AdditionalStocksInformation;
import accelerator.group.brokerapp.Repository.AdditionalStocksInformationRepository;
import accelerator.group.brokerapp.Requests.BuySecurityRequest;
import accelerator.group.brokerapp.Service.DAOService.AdditionalStocksInformationService.AdditionalStocksInformationDAOServiceImpl;
import accelerator.group.brokerapp.Service.DAOService.SecuritiesService.SecuritiesDAOServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdditionalStocksInformationMVCServiceImpl extends AdditionalStocksInformationDAOServiceImpl implements AdditionalStocksInformationMVCService {

    private final SecuritiesDAOServiceImpl securitiesDAOService;
    private final AdditionalStocksInformationDAOServiceImpl additionalStocksInformationDAOService;
    @Autowired
    public AdditionalStocksInformationMVCServiceImpl(AdditionalStocksInformationRepository additionalStocksInformationRepository,
                                                     SecuritiesDAOServiceImpl securitiesDAOService,
                                                     AdditionalStocksInformationDAOServiceImpl additionalStocksInformationDAOService) {
        super(additionalStocksInformationRepository);
        this.securitiesDAOService = securitiesDAOService;
        this.additionalStocksInformationDAOService = additionalStocksInformationDAOService;
    }

    @Override
    public int getLotOfSecurity(String ticker) {
        var security = securitiesDAOService.findSecurityByTicker(ticker);
        return additionalStocksInformationDAOService.findAddStocksInfoById(security.getId()).getLot();
    }

    @Override
    public AdditionalStocksInformation checkAdditionalStockInfo(BuySecurityRequest buySecurityRequest) {
        return additionalStocksInformationDAOService.findAddStocksInfoById(securitiesDAOService.findSecurityByTicker(buySecurityRequest.getTicker()).getId());
    }
}
