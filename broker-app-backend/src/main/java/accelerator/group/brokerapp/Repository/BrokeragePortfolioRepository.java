package accelerator.group.brokerapp.Repository;

import accelerator.group.brokerapp.Entity.BrokeragePortfolio;
import accelerator.group.brokerapp.Entity.Securities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BrokeragePortfolioRepository extends JpaRepository<BrokeragePortfolio, UUID> {

    @Query(name = "FindUserssSecurities", nativeQuery = true)
    List<Securities> findUsersSecuritiesById(@Param(value = "uid") UUID uid);

    @Query(value = "SELECT * FROM brokerage_portfolio b WHERE cast(b.user_id as varchar(255)) = cast(?1 as varchar(255))", nativeQuery = true)
    BrokeragePortfolio findPortfolioByUserId(@Param(value = "uuid") UUID uuid);

}
