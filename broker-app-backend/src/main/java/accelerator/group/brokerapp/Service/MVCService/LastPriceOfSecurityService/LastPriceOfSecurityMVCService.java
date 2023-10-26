package accelerator.group.brokerapp.Service.MVCService.LastPriceOfSecurityService;

import accelerator.group.brokerapp.Entity.LastPriceOfSecurities;
import accelerator.group.brokerapp.Requests.BuySecurityRequest;

import java.util.Optional;

public interface LastPriceOfSecurityMVCService {

    Optional<LastPriceOfSecurities> checkLastPrice(BuySecurityRequest buySecurityRequest);
}
