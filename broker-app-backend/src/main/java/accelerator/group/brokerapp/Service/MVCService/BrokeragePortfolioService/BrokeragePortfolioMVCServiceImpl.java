package accelerator.group.brokerapp.Service.MVCService.BrokeragePortfolioService;

import accelerator.group.brokerapp.Repository.BrokeragePortfolioRepository;
import accelerator.group.brokerapp.Service.DAOService.BrokeragePortfolioService.BrokeragePortfolioDAOServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("BrokeragePortfolioMVCService")
public class BrokeragePortfolioMVCServiceImpl extends BrokeragePortfolioDAOServiceImpl implements BrokeragePortfolioMVCService {
    @Autowired
    public BrokeragePortfolioMVCServiceImpl(BrokeragePortfolioRepository brokeragePortfolioRepository) {
        super(brokeragePortfolioRepository);
    }
}
