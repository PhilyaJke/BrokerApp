package accelerator.group.brokerapp.Service.MVCService.SecuritiesService;

import accelerator.group.brokerapp.Repository.*;
import accelerator.group.brokerapp.Service.DAOService.SecuritiesService.SecuritiesDAOServiceImpl;
import accelerator.group.brokerapp.Service.TinkoffInvestApiService.TinkoffInvestApiServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;

import java.util.List;

@Service
public class SecuritiesMVCServiceImpl extends SecuritiesDAOServiceImpl implements SecuritiesMVCService {

    private final SecuritiesDAOServiceImpl securitiesDAOService;
    private final TinkoffInvestApiServiceImpl tinkoffInvestApiService;

    @Autowired
    public SecuritiesMVCServiceImpl(SecuritiesDAOServiceImpl securitiesDAOService,
                                    SecuritiesRepository securitiesRepository,
                                    TinkoffInvestApiServiceImpl tinkoffInvestApiService) {
        super(securitiesRepository);
        this.securitiesDAOService = securitiesDAOService;
        this.tinkoffInvestApiService = tinkoffInvestApiService;
    }

    @Override
    public List<HistoricCandle> getSecuritiesInfoFromApiByTicker(String ticker) {
        var security = securitiesDAOService.findSecurityByTicker(ticker);
        return tinkoffInvestApiService.getSecuritiesInfoFromApi(security.getFigi());
    }


}