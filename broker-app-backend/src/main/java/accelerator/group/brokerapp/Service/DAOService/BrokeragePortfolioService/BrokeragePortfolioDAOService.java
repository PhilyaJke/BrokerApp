package accelerator.group.brokerapp.Service.DAOService.BrokeragePortfolioService;

import accelerator.group.brokerapp.Entity.BrokeragePortfolio;

import java.util.UUID;

public interface BrokeragePortfolioDAOService {
    BrokeragePortfolio findPortfolioByUserId(UUID uuid);

    void saveBrokeragePortfolio(BrokeragePortfolio brokeragePortfolio);
}
