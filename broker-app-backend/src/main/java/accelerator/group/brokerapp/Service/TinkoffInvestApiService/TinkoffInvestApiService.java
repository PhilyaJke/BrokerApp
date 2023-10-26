package accelerator.group.brokerapp.Service.TinkoffInvestApiService;

import ru.tinkoff.piapi.core.InvestApi;

import java.util.List;

public interface TinkoffInvestApiService {

    InvestApi returnInvestApiConnection();

    List getSecuritiesInfoFromApi(String figi);

}
