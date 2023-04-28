package accelerator.group.brokerapp.Repository;

import accelerator.group.brokerapp.Entity.BrokeragePortfolio;
import accelerator.group.brokerapp.Entity.Securities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BrokeragePortfolioRepository extends JpaRepository<BrokeragePortfolio, UUID>{

    @Query(name = "FindUsersSecurities", nativeQuery = true)
    List<Securities> findSecuritiesByUser(@Param(value = "id") UUID id);

    @Query(value = "SELECT b FROM brokerage_portfolio b INNER JOIN brokerage_portfolio_brokerage_portfolio_securities bpbps on b.id = bpbps.brokerage_portfolio_id " +
            "WHERE cast(b.user_id as varchar(255)) LIKE cast(?1 as varchar(255)) AND bpbps.brokerage_portfolio_securities_id = ?2", nativeQuery = true)
    BrokeragePortfolio findPortfolioByUserIdAndBrokeragePortfolioSecurities(@Param(value = "uid") UUID uid, @Param(value = "id") long id);

    @Query(value = "SELECT b FROM brokerage_portfolio b WHERE cast(b.user_id as varchar(255)) LIKE cast(?1 as varchar(255))", nativeQuery = true)
    BrokeragePortfolio findPortfolioByUserId(@Param(value = "uid") UUID uid);
}
