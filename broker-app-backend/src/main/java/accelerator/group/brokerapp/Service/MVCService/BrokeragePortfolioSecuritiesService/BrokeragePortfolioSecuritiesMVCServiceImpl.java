package accelerator.group.brokerapp.Service.MVCService.BrokeragePortfolioSecuritiesService;

import accelerator.group.brokerapp.Entity.BrokeragePortfolioSecurities;
import accelerator.group.brokerapp.Repository.BrokeragePortfolioSecuritiesRepository;
import accelerator.group.brokerapp.Requests.BuySecurityRequest;
import accelerator.group.brokerapp.Service.DAOService.BrokeragePortfolioSecuritiesService.BrokeragePortfolioSecuritiesDAOServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.AdditionalStocksInformationService.AdditionalStocksInformationMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.BrokeragePortfolioService.BrokeragePortfolioMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.SecuritiesService.SecuritiesMVCServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("BrokeragePortfolioSecuritiesMVCService")
public class BrokeragePortfolioSecuritiesMVCServiceImpl extends BrokeragePortfolioSecuritiesDAOServiceImpl implements BrokeragePortfolioSecuritiesMVCService{

    private final SecuritiesMVCServiceImpl securitiesMVCService;
    private final BrokeragePortfolioSecuritiesDAOServiceImpl brokeragePortfolioSecuritiesDAOService;
    private final BrokeragePortfolioMVCServiceImpl brokeragePortfolioMVCService;
    private final AdditionalStocksInformationMVCServiceImpl additionalStocksInformationMVCService;

    @Autowired
    public BrokeragePortfolioSecuritiesMVCServiceImpl(BrokeragePortfolioSecuritiesRepository brokeragePortfolioSecuritiesRepository,
                                                      @Qualifier("SecuritiesMVCService") SecuritiesMVCServiceImpl securitiesMVCService,
                                                      @Qualifier("BrokeragePortfolioSecuritiesDAOService") BrokeragePortfolioSecuritiesDAOServiceImpl brokeragePortfolioSecuritiesDAOService,
                                                      @Qualifier("BrokeragePortfolioMVCService") BrokeragePortfolioMVCServiceImpl brokeragePortfolioMVCService,
                                                      @Qualifier("AdditionalStocksInformationMVCService") AdditionalStocksInformationMVCServiceImpl additionalStocksInformationMVCService) {
        super(brokeragePortfolioSecuritiesRepository);
        this.securitiesMVCService = securitiesMVCService;
        this.brokeragePortfolioSecuritiesDAOService = brokeragePortfolioSecuritiesDAOService;
        this.brokeragePortfolioMVCService = brokeragePortfolioMVCService;
        this.additionalStocksInformationMVCService = additionalStocksInformationMVCService;
    }

    @Override
    public void sellSecurities(UUID uuid, BuySecurityRequest buySecurityRequest) {
        var securities = securitiesMVCService.findSecurityByTicker(buySecurityRequest.getTicker());
        var portfolio = brokeragePortfolioSecuritiesDAOService.findPortfolioByUserIdAndSecurityId(uuid, securities.getId());
        var subPortfolio = brokeragePortfolioSecuritiesDAOService.findBrokeragePortfolioSecuritiesByPortfolioId(portfolio.getId()).get();
        var count =  buySecurityRequest.getValue()*additionalStocksInformationMVCService.findAddStocksInfoById(securities.getId()).getLot();
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
        var brokeragePortfolio = brokeragePortfolioMVCService.findPortfolioByUserId(uuid);
        BrokeragePortfolioSecurities brokeragePortfolioSecurities = new BrokeragePortfolioSecurities(
                securitiesMVCService.findSecurityByTicker(buySecurityRequest.getTicker()),
                brokeragePortfolio,
                buySecurityRequest.getValue()*additionalStocksInformationMVCService.findAddStocksInfoById(securitiesMVCService.findSecurityByTicker(buySecurityRequest.getTicker()).getId()).getLot()
        );
        brokeragePortfolioSecuritiesDAOService.saveBrokeragePortfolioSecuritiesEntity(brokeragePortfolioSecurities);
    }

    @Override
    public void addPurchaseOfSecurityToPortfolio(Long portfolioId, BuySecurityRequest buySecurityRequest) {
        var subPortfolio = brokeragePortfolioSecuritiesDAOService.findBrokeragePortfolioSecuritiesByPortfolioId(portfolioId).get();
        subPortfolio.setCount(
                subPortfolio.getCount() + buySecurityRequest.getValue()*additionalStocksInformationMVCService.findAddStocksInfoById(securitiesMVCService.findSecurityByTicker(buySecurityRequest.getTicker()).getId()).getLot()
        );
        brokeragePortfolioSecuritiesDAOService.saveBrokeragePortfolioSecuritiesEntity(subPortfolio);
    }

    @Override
    public BrokeragePortfolioSecurities findPortfolioByUserIdAndTicker(UUID uuid, String ticker) {
        var security = securitiesMVCService.findSecurityByTicker(ticker);
        return brokeragePortfolioSecuritiesDAOService.findPortfolioByUserIdAndSecurityId(uuid, security.getId());
    }
}
