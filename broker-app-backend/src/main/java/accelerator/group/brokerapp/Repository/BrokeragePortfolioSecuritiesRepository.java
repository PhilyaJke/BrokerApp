package accelerator.group.brokerapp.Repository;

import accelerator.group.brokerapp.Entity.BrokeragePortfolio;
import accelerator.group.brokerapp.Entity.BrokeragePortfolioSecurities;
import accelerator.group.brokerapp.Entity.Securities;
import accelerator.group.brokerapp.Responses.SecuritiesFullInfoResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BrokeragePortfolioSecuritiesRepository extends JpaRepository<BrokeragePortfolioSecurities, Long> {

    @Query(name = "FindCurrentUsersSecurities", nativeQuery = true)
    BrokeragePortfolioSecurities findPortfolioByUserIdAndSecurityId(@Param(value = "uid") UUID uid, @Param(value = "id") long id);

    @Query(name = "FindUsersSecurities", nativeQuery = true)
    List<SecuritiesFullInfoResponse> findUsersSecuritiesById(@Param(value = "uid") UUID uid);
}
