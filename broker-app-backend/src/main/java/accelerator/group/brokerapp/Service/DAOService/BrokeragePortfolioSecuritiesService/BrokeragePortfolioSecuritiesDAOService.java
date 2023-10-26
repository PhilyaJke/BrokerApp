package accelerator.group.brokerapp.Service.DAOService.BrokeragePortfolioSecuritiesService;

import accelerator.group.brokerapp.Entity.BrokeragePortfolioSecurities;
import accelerator.group.brokerapp.Responses.SecuritiesFullInfoResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BrokeragePortfolioSecuritiesDAOService {

    BrokeragePortfolioSecurities findPortfolioByUserIdAndSecurityId(UUID uuid, Long id);
    Optional<BrokeragePortfolioSecurities> findBrokeragePortfolioSecuritiesByPortfolioId(Long id);
    void deleteBrokeragePortfolioSecuritiesById(Long id);
    void saveBrokeragePortfolioSecuritiesEntity(BrokeragePortfolioSecurities brokeragePortfolioSecurities);

    List<SecuritiesFullInfoResponse> findUsersSecuritiesById(UUID uuid);

}
