package accelerator.group.brokerapp.Service.MVCService.SecuritiesService;

import accelerator.group.brokerapp.Entity.User;
import accelerator.group.brokerapp.Requests.BuySecurityRequest;
import accelerator.group.brokerapp.Responses.SecuritiesPageResponse;
import org.json.JSONObject;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;

import java.util.List;

public interface SecuritiesMVCService {
    List<HistoricCandle> getSecuritiesInfoFromApiByTicker(String ticker);

    SecuritiesPageResponse showSecurities(int page, int size, String region);

    JSONObject findFullInfo(String ticker);

    JSONObject buildJson(BuySecurityRequest buySecurityRequest);

    JSONObject buySecurity( BuySecurityRequest buySecurityRequest, User user);

    JSONObject sellSecurity(BuySecurityRequest buySecurityRequest, User user);

    Double parseToDoublePrice(long units, int nano);
}