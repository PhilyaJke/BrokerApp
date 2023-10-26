package accelerator.group.brokerapp.Service.MVCService.AdditionalStocksInformationService;

import accelerator.group.brokerapp.Entity.AdditionalStocksInformation;
import accelerator.group.brokerapp.Requests.BuySecurityRequest;

public interface AdditionalStocksInformationMVCService {

    int getLotOfSecurity(String ticker);
    AdditionalStocksInformation checkAdditionalStockInfo(BuySecurityRequest buySecurityRequest);
}
