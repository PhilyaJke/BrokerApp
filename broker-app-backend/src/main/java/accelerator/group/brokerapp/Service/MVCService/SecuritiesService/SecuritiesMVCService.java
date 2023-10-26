package accelerator.group.brokerapp.Service.MVCService.SecuritiesService;

import accelerator.group.brokerapp.Entity.AdditionalStocksInformation;
import accelerator.group.brokerapp.Entity.BrokeragePortfolioSecurities;
import accelerator.group.brokerapp.Entity.LastPriceOfSecurities;
import accelerator.group.brokerapp.Requests.BuySecurityRequest;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SecuritiesMVCService {
    List<HistoricCandle> getSecuritiesInfoFromApiByTicker(String ticker);
}