package accelerator.group.brokerapp.Repository;

import accelerator.group.brokerapp.Entity.BrokeragePortfolio;
import accelerator.group.brokerapp.Entity.BrokeragePortfolioSecurities;
import accelerator.group.brokerapp.Entity.Securities;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrokeragePortfolioSecuritiesRepository extends JpaRepository<BrokeragePortfolioSecurities, Long> {

}
