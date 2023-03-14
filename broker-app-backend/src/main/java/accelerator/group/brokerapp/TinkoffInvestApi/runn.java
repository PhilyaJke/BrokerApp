package accelerator.group.brokerapp.TinkoffInvestApi;

import static ru.tinkoff.piapi.contract.v1.MarketDataServiceGrpc.MarketDataServiceStub;
import ru.tinkoff.piapi.contract.v1.GetCandlesRequest;
import ru.tinkoff.piapi.contract.v1.GetCandlesResponse;
import ru.tinkoff.piapi.contract.v1.MarketDataServiceGrpc;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.utils.DateUtils;
import ru.tinkoff.piapi.core.utils.Helpers;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;

public class runn {

    private final MarketDataServiceStub marketDataServiceStub;

    public runn(MarketDataServiceGrpc.MarketDataServiceStub marketDataServiceStub) {
        this.marketDataServiceStub = marketDataServiceStub;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String token = "t.2lxQV0lbcELMPVCsITrhbjldejNU7_MIxjW-7nTMaD5hEI4rLQdwF5nuZAiAolpuGKdShnQ6cyaMA1BeyAJ0BA";
        InvestApi investApi = InvestApi.create(token);
        api.getCandlesExample(investApi);
        



    }
}
