package accelerator.group.brokerapp.Rest;

import accelerator.group.brokerapp.Entity.*;
import accelerator.group.brokerapp.Requests.RegistrationRequest;
import accelerator.group.brokerapp.Requests.AuthenticationRequest;
import accelerator.group.brokerapp.Security.JwtTokenProvider;
import accelerator.group.brokerapp.Service.MVCService.BrokeragePortfolioSecuritiesService.BrokeragePortfolioSecuritiesMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.BrokeragePortfolioService.BrokeragePortfolioMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.RefreshTokensService.RefreshTokensMVCServiceImpl;
import accelerator.group.brokerapp.Service.MVCService.UserService.UserMVCServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@Slf4j
public class UserRestApi {

    //TODO сделать mapsid на табличку с рефрешами + сделать нормально количество акций в отдельной сводной таблице

    private PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    private final AuthenticationManager authenticationManager;
    private final RefreshTokensMVCServiceImpl refreshTokensMVCService;
    private final JwtTokenProvider jwtTokenProvider;
    private final BrokeragePortfolioMVCServiceImpl brokeragePortfolioMVCService;
    private final BrokeragePortfolioSecuritiesMVCServiceImpl brokeragePortfolioSecuritiesMVCService;
    private final UserMVCServiceImpl userMVCServiceImpl;


    @Autowired
    public UserRestApi(AuthenticationManager authenticationManager,
                       RefreshTokensMVCServiceImpl refreshTokensMVCService,
                       JwtTokenProvider jwtTokenProvider,
                       BrokeragePortfolioMVCServiceImpl brokeragePortfolioMVCService,
                       BrokeragePortfolioSecuritiesMVCServiceImpl brokeragePortfolioSecuritiesMVCService,
                       UserMVCServiceImpl userMVCServiceImpl) {
        this.authenticationManager = authenticationManager;
        this.refreshTokensMVCService = refreshTokensMVCService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.brokeragePortfolioMVCService = brokeragePortfolioMVCService;
        this.brokeragePortfolioSecuritiesMVCService = brokeragePortfolioSecuritiesMVCService;
        this.userMVCServiceImpl = userMVCServiceImpl;
        ;
    }



    //TODO:: проверить что отправляет фронт на этот эндпоинт - если дает токен, то проверять его на длительность и ...
    @Transactional
    @PostMapping("/api/auth/login")
    public ResponseEntity login(@RequestBody AuthenticationRequest authenticationRequest){
        log.info("Запрос на логин пришел от пользователя - {}", authenticationRequest.getEmail());

        User user = userMVCServiceImpl.findByEmail(authenticationRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User doesn't exist"));

        String AccessToken;
        String RefreshToken;

        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), authenticationRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            AccessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole().name());
            RefreshToken = jwtTokenProvider.createRefreshToken(user.getUsername(), user.getRole().name());

            refreshTokensMVCService.deleteRefreshTokensByUserUUID(user.getId());
            refreshTokensMVCService.saveRefreshToken(user.getId(), RefreshToken);

            log.info("Логин прошел успешно, пользователь - {}", authenticationRequest.getEmail());
            return new ResponseEntity(createResponse(user.getUsername(), AccessToken, RefreshToken), HttpStatus.OK);
        }catch (AuthenticationException exc){
            log.error("Ошибка логина - невервый пароль/мыло - {}", authenticationRequest.getEmail());
            exc.getCause();
            return new ResponseEntity<>("Invalid email/password combination", HttpStatus.UNAUTHORIZED);
        }
    }

    //TODO: пересмотреть строку с созданием user1 - зачем это нужно делать
    @PostMapping("/api/auth/registration")
    public ResponseEntity registration(@RequestBody RegistrationRequest registrationRequest){
        log.info("Запрос на регистрацию пришел от пользователя - {}", registrationRequest.getEmail());

        String AccessToken;
        String RefreshToken;

        if(userMVCServiceImpl.findByEmail(registrationRequest.getEmail()).isPresent()){
            return new ResponseEntity("User already exist", HttpStatus.FORBIDDEN);
        }else{
            //Возможно тут подлянка
            User user = new User(registrationRequest.getUsername(), passwordEncoder().encode(registrationRequest.getPassword()),
                    null, registrationRequest.getEmail(),
                    Role.USER, Status.ACTIVE);

            AccessToken = jwtTokenProvider.createAccessToken(user.getUsername(), user.getRole().name());
            RefreshToken = jwtTokenProvider.createRefreshToken(user.getUsername(), user.getRole().name());
            refreshTokensMVCService.saveRefreshToken(user.getId(), RefreshToken);

            BrokeragePortfolio brokeragePortfolio = new BrokeragePortfolio();
            brokeragePortfolio.setUser(user);
            brokeragePortfolioMVCService.saveBrokeragePortfolio(brokeragePortfolio);

            userMVCServiceImpl.saveUser(user);
            log.info("Регистрация прошла успешно, пользователь - {}", registrationRequest.getEmail());
            return new ResponseEntity(createResponse(user.getUsername(), AccessToken, RefreshToken), HttpStatus.CREATED);
        }
    }

    @GetMapping("/api/auth/updateaccesstoken")
    public ResponseEntity updateAccessToken(@RequestHeader(value = "Authorization") String Authorization){
        log.info("Запрос на обновление access токена");

        User user = userMVCServiceImpl.findByUsername(jwtTokenProvider.getUsername(Authorization)).get();
        String AccessToken = jwtTokenProvider.createAccessToken(user.getUsername(), user.getRole().name());

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", AccessToken);
        return new ResponseEntity(response, HttpStatus.CREATED);
    }

    //Не работает - доделать
    @Transactional
    @GetMapping("/api/auth/logout")
    public void logout(@RequestHeader(value = "Authorization") String Authorization, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        log.info("Запрос на выход из приложения ");
        refreshTokensMVCService.deleteRefreshTokensByToken(Authorization);
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(httpServletRequest, httpServletResponse, null);
    }

    // TODO: пересмотреть, переделать - не работает
    @GetMapping("/api/profile")
    public ResponseEntity userProfile(@RequestHeader(value = "Authorization") String Authorization){
        User user = userMVCServiceImpl.findByUsername(jwtTokenProvider.getUsername(Authorization)).get();
        log.info("Профиль пользователя - {}", user.getUsername());

        var SecuritiesFullInfo = brokeragePortfolioSecuritiesMVCService.findUsersSecuritiesById(user.getId());
        Double sum = userMVCServiceImpl.findTotalSumOfUsersSecurities(SecuritiesFullInfo, user.getId());
        HashMap<String, Object> map = new HashMap<>();
        map.put("username", user.getUsername());
        map.put("budjet", sum);
        map.put("securities", SecuritiesFullInfo);
        return ResponseEntity.ok(map);
    }

    public static Map<String, Object> createResponse(String username, String AccessToken, String RefreshToken){
        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("accessToken", AccessToken);
        response.put("refreshToken", RefreshToken);
        return response;
    }
}
