package accelerator.group.brokerapp.Rest;

import accelerator.group.brokerapp.Entity.*;
import accelerator.group.brokerapp.Repository.*;
import accelerator.group.brokerapp.Requests.RegistrationRequest;
import accelerator.group.brokerapp.Requests.AuthenticationRequest;
import accelerator.group.brokerapp.Responses.SecuritiesFullInfoResponse;
import accelerator.group.brokerapp.Security.JwtTokenProvider;
import accelerator.group.brokerapp.Service.UserService.UserServiceImpl;
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
    private final SecuritiesRepository securitiesRepository;
    private final UserRepository userRepository;
    private final RefreshTokensRepository refreshTokensRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserServiceImpl userService;
    private final BrokeragePortfolioRepository brokeragePortfolioRepository;
    private final BrokeragePortfolioSecuritiesRepository brokeragePortfolioSecuritiesRepository;
    private final AdditionalStocksInformationRepository additionalStocksInformationRepository;

    @Autowired
    public UserRestApi(SecuritiesRepository securitiesRepository,
                       UserRepository userRepository,
                       RefreshTokensRepository refreshTokensRepository,
                       JwtTokenProvider jwtTokenProvider,
                       AuthenticationManager authenticationManager,
                       UserServiceImpl userService,
                       BrokeragePortfolioRepository brokeragePortfolioRepository,
                       BrokeragePortfolioSecuritiesRepository brokeragePortfolioSecuritiesRepository,
                       AdditionalStocksInformationRepository additionalStocksInformationRepository) {
        this.securitiesRepository = securitiesRepository;
        this.userRepository = userRepository;
        this.refreshTokensRepository = refreshTokensRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.brokeragePortfolioRepository = brokeragePortfolioRepository;
        this.brokeragePortfolioSecuritiesRepository = brokeragePortfolioSecuritiesRepository;
        this.additionalStocksInformationRepository = additionalStocksInformationRepository;
    }

    @Transactional
    @PostMapping("/api/auth/login")
    public ResponseEntity login(@RequestBody AuthenticationRequest authenticationRequest){
        log.info("Запрос на логин пришел от пользователя - {}", authenticationRequest.getEmail());
        User user = userRepository.findByEmail(authenticationRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User doesnt exist"));

        String AccessToken;
        String RefreshToken;

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), authenticationRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            AccessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole().name());
            RefreshToken = jwtTokenProvider.createRefreshToken(user.getUsername(), user.getRole().name());
            if(refreshTokensRepository.FindRefreshTokenByUserUUID(userRepository.findByUsername(user.getUsername()).get().getId())!=null){
                refreshTokensRepository.deleteRefreshTokensByUserUUID(userRepository.findByUsername(user.getUsername()).get().getId());
            }
            RefreshTokens refreshTokens = new RefreshTokens();
            refreshTokens.setToken(RefreshToken);
            refreshTokens.setUserUUID(user.getId());
            refreshTokensRepository.save(refreshTokens);
            Map<String, Object> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("accessToken", AccessToken);
            response.put("refreshToken", RefreshToken);
            log.info("Логин прошел успешно, пользователь - {}", authenticationRequest.getEmail());
            return ResponseEntity.ok(response);
        }catch (AuthenticationException exc){
            log.error("Ошибка логина - невервый пароль/мыло - {}", authenticationRequest.getEmail());
            exc.getCause();
            return new ResponseEntity<>("Invalid email/password combination", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/api/auth/registration")
    public ResponseEntity registration(@RequestBody RegistrationRequest registrationRequest){
        log.info("Запрос на регистрацию пришел от пользователя - {}", registrationRequest.getEmail());
        String AccessToken;
        String RefreshToken;
        if(userRepository.findByEmail(registrationRequest.getEmail()).isPresent()){
            return new ResponseEntity("User already exist", HttpStatus.FORBIDDEN);
        }else{
            //Возможно тут подлянка
            User user = new User(registrationRequest.getUsername(), passwordEncoder().encode(registrationRequest.getPassword()),
                    null, registrationRequest.getEmail(),
                    Role.USER, Status.ACTIVE);
            BrokeragePortfolio brokeragePortfolio = new BrokeragePortfolio();
            brokeragePortfolio.setUser(user);
            brokeragePortfolioRepository.save(brokeragePortfolio);
            userService.saveUser(user);
            User user1 = userRepository.findByUsername(registrationRequest.getUsername()).get();
            AccessToken = jwtTokenProvider.createAccessToken(user1.getUsername(), user1.getRole().name());
            RefreshToken = jwtTokenProvider.createRefreshToken(user1.getUsername(), user1.getRole().name());
            RefreshTokens refreshTokens = new RefreshTokens();
            refreshTokens.setUserUUID(user1.getId());
            refreshTokens.setToken(RefreshToken);
            refreshTokensRepository.save(refreshTokens);
            Map<String, Object> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("accessToken", AccessToken);
            response.put("refreshToken", RefreshToken);
            log.info("Регистрация прошла успешно, пользователь - {}", registrationRequest.getEmail());
            return new ResponseEntity(response, HttpStatus.CREATED);
        }
    }

    @GetMapping("/api/auth/updateaccesstoken")
    public ResponseEntity updateAccessToken(@RequestHeader(value = "Authorization") String Authorization){
        log.info("Запрос на обновление access токена");
        User user = userRepository.findByUsername(jwtTokenProvider.getUsername(Authorization)).get();
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
        refreshTokensRepository.deleteRefreshTokensByToken(Authorization);
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(httpServletRequest, httpServletResponse, null);
    }

    @GetMapping("/api/profile")
    public ResponseEntity userProfile(@RequestHeader(value = "Authorization") String Authorization){
        User user = userRepository.findByUsername(jwtTokenProvider.getUsername(Authorization)).get();
        log.info("Профиль пользователя - {}", user.getUsername());
        var SecuritiesFullInfo = brokeragePortfolioSecuritiesRepository.findUsersSecuritiesById(user.getId());
        Double sum = 0.0;
        for(SecuritiesFullInfoResponse sec: SecuritiesFullInfo) {
            var portfolio = brokeragePortfolioSecuritiesRepository.findPortfolioByUserIdAndSecurityId(
                    userRepository.UserProfileInfo(user.getUsername()).getId(), securitiesRepository.findByTicker(sec.getTicker()).get().getId());
            var addStockInfo = additionalStocksInformationRepository.findAddStocksInfoById(sec.getId());
            sum+=brokeragePortfolioSecuritiesRepository.findById(portfolio.getId()).get().getCount() * addStockInfo.getPrice();
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("username", user.getUsername());
        map.put("budjet", sum);
        map.put("securities", SecuritiesFullInfo);
        return ResponseEntity.ok(map);
    }
}
