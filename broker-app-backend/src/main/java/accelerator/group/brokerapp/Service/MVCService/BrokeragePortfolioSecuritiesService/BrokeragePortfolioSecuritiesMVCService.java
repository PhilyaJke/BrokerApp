package accelerator.group.brokerapp.Service.MVCService.BrokeragePortfolioSecuritiesService;

import accelerator.group.brokerapp.Entity.BrokeragePortfolioSecurities;
import accelerator.group.brokerapp.Entity.Securities;
import accelerator.group.brokerapp.Requests.BuySecurityRequest;

import java.util.List;
import java.util.UUID;

public interface BrokeragePortfolioSecuritiesMVCService {

    void sellSecurities(UUID uuid, BuySecurityRequest buySecurityRequest);

    void createPortfolioAndAddPurchaseOfSecurity(UUID uuid, BuySecurityRequest buySecurityRequest);

    void addPurchaseOfSecurityToPortfolio(Long portfolioId, BuySecurityRequest buySecurityRequest);

    BrokeragePortfolioSecurities findPortfolioByUserIdAndTicker(UUID uuid, String ticker);
}
