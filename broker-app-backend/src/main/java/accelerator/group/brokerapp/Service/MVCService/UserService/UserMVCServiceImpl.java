package accelerator.group.brokerapp.Service.MVCService.UserService;

import accelerator.group.brokerapp.Entity.BrokeragePortfolio;
import accelerator.group.brokerapp.Entity.Role;
import accelerator.group.brokerapp.Entity.Status;
import accelerator.group.brokerapp.Entity.User;
import accelerator.group.brokerapp.Repository.UserRepository;
import accelerator.group.brokerapp.Requests.AuthenticationRequest;
import accelerator.group.brokerapp.Requests.RegistrationRequest;
import accelerator.group.brokerapp.Responses.SecuritiesFullInfoResponse;
import accelerator.group.brokerapp.Security.JwtTokenProvider;
import accelerator.group.brokerapp.Service.DAOService.UserService.UserDAOServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.AdditionalStocksInformationService.AdditionalStocksInformationMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.BrokeragePortfolioSecuritiesService.BrokeragePortfolioSecuritiesMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.BrokeragePortfolioService.BrokeragePortfolioMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.RefreshTokensService.RefreshTokensMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.SecuritiesService.SecuritiesMVCServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service("UserMVCService")
public class UserMVCServiceImpl extends UserDAOServiceImpl implements UserMVCService {

    private PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    private final BrokeragePortfolioSecuritiesMVCServiceImpl brokeragePortfolioSecuritiesMVCService;
    private final BrokeragePortfolioMVCServiceImpl brokeragePortfolioMVCService;
    private final AdditionalStocksInformationMVCServiceImpl additionalStocksInformationMVCService;
    private final UserDAOServiceImpl userDAOService;
    private final RefreshTokensMVCServiceImpl refreshTokensMVCService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;


    @Autowired
    public UserMVCServiceImpl(UserRepository userRepository,
                              @Qualifier("BrokeragePortfolioSecuritiesMVCService") BrokeragePortfolioSecuritiesMVCServiceImpl brokeragePortfolioSecuritiesMVCService,
                              @Qualifier("BrokeragePortfolioMVCService") BrokeragePortfolioMVCServiceImpl brokeragePortfolioMVCService,
                              @Qualifier("AdditionalStocksInformationMVCService") AdditionalStocksInformationMVCServiceImpl additionalStocksInformationMVCService,
                              @Qualifier("UserDAOService") UserDAOServiceImpl userDAOService,
                              @Qualifier("RefreshTokensMVCService") RefreshTokensMVCServiceImpl refreshTokensMVCService,
                              JwtTokenProvider jwtTokenProvider,
                              AuthenticationManager authenticationManager) {
        super(userRepository);
        this.brokeragePortfolioSecuritiesMVCService = brokeragePortfolioSecuritiesMVCService;
        this.brokeragePortfolioMVCService = brokeragePortfolioMVCService;
        this.additionalStocksInformationMVCService = additionalStocksInformationMVCService;
        this.userDAOService = userDAOService;
        this.refreshTokensMVCService = refreshTokensMVCService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }


    //TODO:: возможно тут нужно искать айди акции так: securitiesDAOService.findSecurityByTicker(sec.getTicker()).getId())
    @Override
    public Double findTotalSumOfUsersSecurities(List<SecuritiesFullInfoResponse> securitiesFullInfoResponse, UUID uuid) {
        Double sum = 0.0;
        for(SecuritiesFullInfoResponse sec: securitiesFullInfoResponse) {
            var portfolio = brokeragePortfolioSecuritiesMVCService.findPortfolioByUserIdAndSecurityId(
                    uuid, sec.getId());
            var addStockInfo = additionalStocksInformationMVCService.findAddStocksInfoById(sec.getId());
            sum+=brokeragePortfolioSecuritiesMVCService.findBrokeragePortfolioSecuritiesByPortfolioId(portfolio.getId()).get().getCount() * addStockInfo.getPrice();
        }
        return sum;
    }

    @Override
    public JSONObject login(AuthenticationRequest authenticationRequest, User user) {

        String AccessToken;
        String RefreshToken;

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), authenticationRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AccessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole().name());
        RefreshToken = jwtTokenProvider.createRefreshToken(user.getUsername(), user.getRole().name());

        refreshTokensMVCService.deleteRefreshTokensByUserUUID(user.getId());
        refreshTokensMVCService.saveRefreshToken(user.getId(), RefreshToken);

        return createResponse(user.getUsername(), AccessToken, RefreshToken);
    }

    @Override
    public JSONObject registration(RegistrationRequest registrationRequest) {

        String AccessToken;
        String RefreshToken;

        User user = new User(registrationRequest.getUsername(), passwordEncoder().encode(registrationRequest.getPassword()),
                null, registrationRequest.getEmail(),
                Role.USER, Status.ACTIVE);

        AccessToken = jwtTokenProvider.createAccessToken(user.getUsername(), user.getRole().name());
        RefreshToken = jwtTokenProvider.createRefreshToken(user.getUsername(), user.getRole().name());
        refreshTokensMVCService.saveRefreshToken(user.getId(), RefreshToken);

        BrokeragePortfolio brokeragePortfolio = new BrokeragePortfolio();
        brokeragePortfolio.setUser(user);
        brokeragePortfolioMVCService.saveBrokeragePortfolio(brokeragePortfolio);

        userDAOService.saveUser(user);

        return createResponse(user.getUsername(), AccessToken, RefreshToken);
    }

    @Override
    public JSONObject updateAccessToken(String Authorization, User user) {
        String AccessToken = jwtTokenProvider.createAccessToken(user.getUsername(), user.getRole().name());
        JSONObject jsonObject = new JSONObject();
        jsonObject.append("accessToken", AccessToken);
        return jsonObject;
    }

    @Override
    public void logout(String Authorization, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        refreshTokensMVCService.deleteRefreshTokensByToken(Authorization);
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(httpServletRequest, httpServletResponse, null);
    }

    @Override
    public JSONObject userProfile(String Authorization, User user) {
        var SecuritiesFullInfo = brokeragePortfolioSecuritiesMVCService.findUsersSecuritiesById(user.getId());
        Double sum = findTotalSumOfUsersSecurities(SecuritiesFullInfo, user.getId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.append("username", user.getUsername());
        jsonObject.append("budjet", sum);
        jsonObject.append("securities", SecuritiesFullInfo);
        return jsonObject;
    }

    @Override
    public JSONObject createResponse(String username, String AccessToken, String RefreshToken) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.append("username", username);
        jsonObject.append("accessToken", AccessToken);
        jsonObject.append("refreshToken", RefreshToken);
        return jsonObject;
    }
}