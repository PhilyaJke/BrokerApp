package accelerator.group.brokerapp.Service.MVCService.BrokeragePortfolioSecuritiesService;

import accelerator.group.brokerapp.Entity.BrokeragePortfolioSecurities;
import accelerator.group.brokerapp.Entity.Securities;
import accelerator.group.brokerapp.Repository.BrokeragePortfolioSecuritiesRepository;
import accelerator.group.brokerapp.Requests.BuySecurityRequest;
import accelerator.group.brokerapp.Service.DAOService.AdditionalStocksInformationService.AdditionalStocksInformationDAOServiceImpl;
import accelerator.group.brokerapp.Service.DAOService.BrokeragePortfolioSecuritiesService.BrokeragePortfolioSecuritiesDAOServiceImpl;
import accelerator.group.brokerapp.Service.DAOService.BrokeragePortfolioService.BrokeragePortfolioDAOServiceImpl;
import accelerator.group.brokerapp.Service.DAOService.SecuritiesService.SecuritiesDAOServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BrokeragePortfolioSecuritiesMVCServiceImpl extends BrokeragePortfolioSecuritiesDAOServiceImpl implements BrokeragePortfolioSecuritiesMVCService{

    private final SecuritiesDAOServiceImpl securitiesDAOService;
    private final BrokeragePortfolioSecuritiesDAOServiceImpl brokeragePortfolioSecuritiesDAOService;
    private final BrokeragePortfolioDAOServiceImpl brokeragePortfolioDAOService;
    private final AdditionalStocksInformationDAOServiceImpl additionalStocksInformationDAOService;

    @Autowired
    public BrokeragePortfolioSecuritiesMVCServiceImpl(BrokeragePortfolioSecuritiesRepository brokeragePortfolioSecuritiesRepository,
                                                      SecuritiesDAOServiceImpl securitiesDAOService,
                                                      BrokeragePortfolioSecuritiesDAOServiceImpl brokeragePortfolioSecuritiesDAOService,
                                                      BrokeragePortfolioDAOServiceImpl brokeragePortfolioDAOService,
                                                      AdditionalStocksInformationDAOServiceImpl additionalStocksInformationDAOService) {
        super(brokeragePortfolioSecuritiesRepository);
        this.securitiesDAOService = securitiesDAOService;
        this.brokeragePortfolioSecuritiesDAOService = brokeragePortfolioSecuritiesDAOService;
        this.brokeragePortfolioDAOService = brokeragePortfolioDAOService;
        this.additionalStocksInformationDAOService = additionalStocksInformationDAOService;
    }

    @Override
    public void sellSecurities(UUID uuid, BuySecurityRequest buySecurityRequest) {
        var securities = securitiesDAOService.findSecurityByTicker(buySecurityRequest.getTicker());
        var portfolio = brokeragePortfolioSecuritiesDAOService.findPortfolioByUserIdAndSecurityId(uuid, securities.getId());
        var subPortfolio = brokeragePortfolioSecuritiesDAOService.findBrokeragePortfolioSecuritiesByPortfolioId(portfolio.getId()).get();
        var count =  buySecurityRequest.getValue()*additionalStocksInformationDAOService.findAddStocksInfoById(securities.getId()).getLot();
        if(subPortfolio != null && subPortfolio.getCount() > buySecurityRequest.getValue()){
            subPortfolio.setCount(
                    subPortfolio.getCount()-count
            );
        }else if(portfolio != null && subPortfolio.getCount() == count){
            brokeragePortfolioSecuritiesDAOService.deleteBrokeragePortfolioSecuritiesById(subPortfolio.getId());
        }
    }

    @Override
    public void createPortfolioAndAddPurchaseOfSecurity(UUID uuid, BuySecurityRequest buySecurityRequest) {
        var brokeragePortfolio = brokeragePortfolioDAOService.findPortfolioByUserId(uuid);
        BrokeragePortfolioSecurities brokeragePortfolioSecurities = new BrokeragePortfolioSecurities(
                securitiesDAOService.findSecurityByTicker(buySecurityRequest.getTicker()),
                brokeragePortfolio,
                buySecurityRequest.getValue()*additionalStocksInformationDAOService.findAddStocksInfoById(securitiesDAOService.findSecurityByTicker(buySecurityRequest.getTicker()).getId()).getLot()
        );
        brokeragePortfolioSecuritiesDAOService.saveBrokeragePortfolioSecuritiesEntity(brokeragePortfolioSecurities);
    }

    @Override
    public void addPurchaseOfSecurityToPortfolio(Long portfolioId, BuySecurityRequest buySecurityRequest) {
        var subPortfolio = brokeragePortfolioSecuritiesDAOService.findBrokeragePortfolioSecuritiesByPortfolioId(portfolioId).get();
        subPortfolio.setCount(
                subPortfolio.getCount() + buySecurityRequest.getValue()*additionalStocksInformationDAOService.findAddStocksInfoById(securitiesDAOService.findSecurityByTicker(buySecurityRequest.getTicker()).getId()).getLot()
        );
        brokeragePortfolioSecuritiesDAOService.saveBrokeragePortfolioSecuritiesEntity(subPortfolio);
    }

    @Override
    public BrokeragePortfolioSecurities findPortfolioByUserIdAndTicker(UUID uuid, String ticker) {
        var security = securitiesDAOService.findSecurityByTicker(ticker);
        return brokeragePortfolioSecuritiesDAOService.findPortfolioByUserIdAndSecurityId(uuid, security.getId());
    }

}
