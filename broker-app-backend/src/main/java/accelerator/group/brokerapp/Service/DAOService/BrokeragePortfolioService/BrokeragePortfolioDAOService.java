package accelerator.group.brokerapp.Service.DAOService.BrokeragePortfolioService;

import accelerator.group.brokerapp.Entity.BrokeragePortfolio;
import accelerator.group.brokerapp.Entity.Securities;

import java.util.List;
import java.util.UUID;

public interface BrokeragePortfolioDAOService {
    BrokeragePortfolio findPortfolioByUserId(UUID uuid);

    void saveBrokeragePortfolio(BrokeragePortfolio brokeragePortfolio);

    List<Securities> findUsersSecuritiesById(UUID uuid);
}
