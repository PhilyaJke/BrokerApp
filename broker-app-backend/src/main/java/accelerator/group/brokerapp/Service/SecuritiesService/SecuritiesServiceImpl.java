package accelerator.group.brokerapp.Service.SecuritiesService;

import accelerator.group.brokerapp.Repository.SecuritiesRepository;
import accelerator.group.brokerapp.Responses.SecuritiesFullInfoResponse;
import accelerator.group.brokerapp.Responses.SecuritiesPageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.core.InvestApi;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Service
public class SecuritiesServiceImpl implements SecuritiesService{

    private final SecuritiesRepository securitiesRepository;
    private InvestApi investApi;

    @Autowired
    public SecuritiesServiceImpl(SecuritiesRepository securitiesRepository,
                                 @Value("${invest.api.secret.token}") String token) {
        this.securitiesRepository = securitiesRepository;
        this.investApi = InvestApi.create(token);
    }

    @Override
    public SecuritiesPageResponse findAllSecuritiesPage(Pageable pageable) {
        List<SecuritiesFullInfoResponse> securitiesPage = securitiesRepository.findAllSecuritiesPage(pageable);
        return new SecuritiesPageResponse(
                securitiesPage
        );
    }

    @Override
    public SecuritiesPageResponse findAllRuSecuritiesPage(Pageable pageable) {
        List<SecuritiesFullInfoResponse> securitiesPage = securitiesRepository.findAllRuSecuritiesPage(pageable);
        return new SecuritiesPageResponse(
                securitiesPage
        );
    }

    @Override
    public SecuritiesPageResponse findAllForeignSecuritiesPage(Pageable pageable) {
        List<SecuritiesFullInfoResponse> securitiesPage = securitiesRepository.findAllForeignSecuritiesPage(pageable);
        return new SecuritiesPageResponse(
                securitiesPage
        );
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
