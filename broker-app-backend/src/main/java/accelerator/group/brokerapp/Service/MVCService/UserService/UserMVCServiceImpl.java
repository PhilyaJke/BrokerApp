package accelerator.group.brokerapp.Service.MVCService.UserService;

import accelerator.group.brokerapp.Entity.Securities;
import accelerator.group.brokerapp.Entity.User;
import accelerator.group.brokerapp.Repository.UserRepository;
import accelerator.group.brokerapp.Responses.SecuritiesFullInfoResponse;
import accelerator.group.brokerapp.Service.DAOService.AdditionalStocksInformationService.AdditionalStocksInformationDAOServiceImpl;
import accelerator.group.brokerapp.Service.DAOService.BrokeragePortfolioSecuritiesService.BrokeragePortfolioSecuritiesDAOServiceImpl;
import accelerator.group.brokerapp.Service.DAOService.SecuritiesService.SecuritiesDAOServiceImpl;
import accelerator.group.brokerapp.Service.DAOService.UserService.UserDAOServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.BrokeragePortfolioSecuritiesService.BrokeragePortfolioSecuritiesMVCServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserMVCServiceImpl extends UserDAOServiceImpl implements UserMVCService {

    private final BrokeragePortfolioSecuritiesMVCServiceImpl brokeragePortfolioSecuritiesMVCService;
    private final BrokeragePortfolioSecuritiesDAOServiceImpl brokeragePortfolioSecuritiesDAOService;
    private final AdditionalStocksInformationDAOServiceImpl additionalStocksInformationDAOService;

    private final SecuritiesDAOServiceImpl securitiesDAOService;

    @Autowired
    public UserMVCServiceImpl(UserRepository userRepository,
                              BrokeragePortfolioSecuritiesMVCServiceImpl brokeragePortfolioSecuritiesMVCService,
                              BrokeragePortfolioSecuritiesDAOServiceImpl brokeragePortfolioSecuritiesDAOService,
                              AdditionalStocksInformationDAOServiceImpl additionalStocksInformationDAOService,
                              SecuritiesDAOServiceImpl securitiesDAOService) {
        super(userRepository);
        this.brokeragePortfolioSecuritiesMVCService = brokeragePortfolioSecuritiesMVCService;
        this.brokeragePortfolioSecuritiesDAOService = brokeragePortfolioSecuritiesDAOService;
        this.additionalStocksInformationDAOService = additionalStocksInformationDAOService;
        this.securitiesDAOService = securitiesDAOService;
    }


    //TODO:: возможно тут нужно искать айди акции так: securitiesDAOService.findSecurityByTicker(sec.getTicker()).getId())
    @Override
    public Double findTotalSumOfUsersSecurities(List<SecuritiesFullInfoResponse> securitiesFullInfoResponse, UUID uuid) {
        Double sum = 0.0;
        for(SecuritiesFullInfoResponse sec: securitiesFullInfoResponse) {
            var portfolio = brokeragePortfolioSecuritiesMVCService.findPortfolioByUserIdAndSecurityId(
                    uuid, sec.getId());
            var addStockInfo = additionalStocksInformationDAOService.findAddStocksInfoById(sec.getId());
            sum+=brokeragePortfolioSecuritiesDAOService.findBrokeragePortfolioSecuritiesByPortfolioId(portfolio.getId()).get().getCount() * addStockInfo.getPrice();
        }
        return sum;
    }
}