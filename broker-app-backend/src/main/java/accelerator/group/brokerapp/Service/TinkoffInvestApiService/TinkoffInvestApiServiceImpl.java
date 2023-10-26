package accelerator.group.brokerapp.Service.TinkoffInvestApiService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.core.InvestApi;

import java.time.Instant;
import java.util.List;

@Service
public class TinkoffInvestApiServiceImpl implements TinkoffInvestApiService {

    private InvestApi investApi;

    public TinkoffInvestApiServiceImpl(@Value("${invest.api.secret.token}") String token) {
        this.investApi = InvestApi.create(token);
    }

    @Override
    public InvestApi returnInvestApiConnection() {
        return investApi;
    }
    @Override
    public List<HistoricCandle> getSecuritiesInfoFromApi(String figi) {
        var candlesDay = investApi.getMarketDataService()
                .getCandlesSync(figi, Instant.ofEpochSecond(Instant.now().minusSeconds(31536000).getEpochSecond()), Instant.now(), CandleInterval.CANDLE_INTERVAL_DAY);
        return candlesDay;
    }
}
