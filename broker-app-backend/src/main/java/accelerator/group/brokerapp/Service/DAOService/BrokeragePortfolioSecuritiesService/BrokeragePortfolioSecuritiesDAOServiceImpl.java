package accelerator.group.brokerapp.Service.DAOService.BrokeragePortfolioSecuritiesService;

import accelerator.group.brokerapp.Entity.BrokeragePortfolioSecurities;
import accelerator.group.brokerapp.Repository.BrokeragePortfolioSecuritiesRepository;
import accelerator.group.brokerapp.Responses.SecuritiesFullInfoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service("BrokeragePortfolioSecuritiesDAOService")
public class BrokeragePortfolioSecuritiesDAOServiceImpl implements BrokeragePortfolioSecuritiesDAOService {

    private final BrokeragePortfolioSecuritiesRepository brokeragePortfolioSecuritiesRepository;

    @Autowired
    public BrokeragePortfolioSecuritiesDAOServiceImpl(BrokeragePortfolioSecuritiesRepository brokeragePortfolioSecuritiesRepository) {
        this.brokeragePortfolioSecuritiesRepository = brokeragePortfolioSecuritiesRepository;
    }

    @Override
    public BrokeragePortfolioSecurities findPortfolioByUserIdAndSecurityId(UUID uuid, Long id) {
        return brokeragePortfolioSecuritiesRepository.findPortfolioByUserIdAndSecurityId(uuid, id);
    }

    @Override
    public Optional<BrokeragePortfolioSecurities> findBrokeragePortfolioSecuritiesByPortfolioId(Long id) {
        return brokeragePortfolioSecuritiesRepository.findById(id);
    }

    @Override
    public void saveBrokeragePortfolioSecuritiesEntity(BrokeragePortfolioSecurities brokeragePortfolioSecurities) {
        brokeragePortfolioSecuritiesRepository.save(brokeragePortfolioSecurities);
    }

    @Override
    public void deleteBrokeragePortfolioSecuritiesById(Long id) {
        brokeragePortfolioSecuritiesRepository.deleteById(id);
    }

    @Override
    public List<SecuritiesFullInfoResponse> findUsersSecuritiesById(UUID uuid) {
        return brokeragePortfolioSecuritiesRepository.findUsersSecuritiesById(uuid);
    }
}
