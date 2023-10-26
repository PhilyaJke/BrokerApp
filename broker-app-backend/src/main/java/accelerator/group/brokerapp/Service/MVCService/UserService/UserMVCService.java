package accelerator.group.brokerapp.Service.MVCService.UserService;

import accelerator.group.brokerapp.Responses.SecuritiesFullInfoResponse;

import java.util.List;
import java.util.UUID;

public interface UserMVCService {

    Double findTotalSumOfUsersSecurities(List<SecuritiesFullInfoResponse> securitiesFullInfoResponse, UUID uuid);
}
