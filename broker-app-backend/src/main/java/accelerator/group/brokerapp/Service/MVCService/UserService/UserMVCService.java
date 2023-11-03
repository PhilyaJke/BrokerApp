package accelerator.group.brokerapp.Service.MVCService.UserService;

import accelerator.group.brokerapp.Entity.User;
import accelerator.group.brokerapp.Requests.AuthenticationRequest;
import accelerator.group.brokerapp.Requests.RegistrationRequest;
import accelerator.group.brokerapp.Responses.SecuritiesFullInfoResponse;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

public interface UserMVCService {

    Double findTotalSumOfUsersSecurities(List<SecuritiesFullInfoResponse> securitiesFullInfoResponse, UUID uuid);

    JSONObject login(AuthenticationRequest authenticationRequest, User user);

    JSONObject registration(RegistrationRequest registrationRequest);

    JSONObject updateAccessToken(String Authorization, User user);

    void logout(String Authorization, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

    JSONObject userProfile(String Authorization, User user);

    JSONObject createResponse(String username, String AccessToken, String RefreshToken);
}
