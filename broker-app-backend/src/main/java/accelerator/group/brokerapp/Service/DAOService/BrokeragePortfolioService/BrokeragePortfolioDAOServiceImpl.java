package accelerator.group.brokerapp.Service.DAOService.BrokeragePortfolioService;

import accelerator.group.brokerapp.Entity.BrokeragePortfolio;
import accelerator.group.brokerapp.Repository.BrokeragePortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BrokeragePortfolioDAOServiceImpl implements BrokeragePortfolioDAOService{

    private final BrokeragePortfolioRepository brokeragePortfolioRepository;

    @Autowired
    public BrokeragePortfolioDAOServiceImpl(BrokeragePortfolioRepository brokeragePortfolioRepository) {
        this.brokeragePortfolioRepository = brokeragePortfolioRepository;
    }

    @Override
    public BrokeragePortfolio findPortfolioByUserId(UUID uuid) {
        return brokeragePortfolioRepository.findPortfolioByUserId(uuid);
    }

    @Override
    public void saveBrokeragePortfolio(BrokeragePortfolio brokeragePortfolio) {
        brokeragePortfolioRepository.save(brokeragePortfolio);
    }
}
