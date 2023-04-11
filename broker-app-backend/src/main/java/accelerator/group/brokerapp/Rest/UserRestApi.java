package accelerator.group.brokerapp.Rest;

import accelerator.group.brokerapp.Entity.RefreshTokens;
import accelerator.group.brokerapp.Entity.Role;
import accelerator.group.brokerapp.Entity.Status;
import accelerator.group.brokerapp.Entity.User;
import accelerator.group.brokerapp.Repository.RefreshTokensRepository;
import accelerator.group.brokerapp.Repository.UserRepository;
import accelerator.group.brokerapp.Requests.RegistrationRequest;
import accelerator.group.brokerapp.Requests.AuthenticationRequest;
import accelerator.group.brokerapp.Security.JwtTokenProvider;
import accelerator.group.brokerapp.Service.UserService.UserServiceImpl;
import com.owlike.genson.Genson;
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

// Переписать все создание токенов в один метод
// Поправить названия (auth -> login)
@RestController
@CrossOrigin(origins = "http://localhost:5173")
@Slf4j
public class UserRestApi {

    private PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    private final UserRepository userRepository;
    private final RefreshTokensRepository refreshTokensRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserServiceImpl userService;

    @Autowired
    public UserRestApi(UserRepository userRepository,
                       RefreshTokensRepository refreshTokensRepository, JwtTokenProvider jwtTokenProvider,
                       AuthenticationManager authenticationManager, UserServiceImpl userService) {
        this.userRepository = userRepository;
        this.refreshTokensRepository = refreshTokensRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @Transactional
    @PostMapping("/api/auth/login")
    public ResponseEntity login(@RequestBody AuthenticationRequest authenticationRequest){
        log.info("Запрос на логин пришел от пользователя - {}", authenticationRequest.getEmail());
        User user = userRepository.findByEmail(authenticationRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User doesnt exist"));


        String AccessToken = "";
        String RefreshToken = "";
        try {
            log.info("Запрос на логин пришел от пользователя 1 - {}", authenticationRequest.getEmail());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), authenticationRequest.getPassword()));
            log.info("Запрос на логин пришел от пользователя 2 - {}", authenticationRequest.getEmail());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Запрос на логин пришел от пользователя 3 - {}", authenticationRequest.getEmail());
            AccessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole().name());
            RefreshToken = jwtTokenProvider.createRefreshToken(user.getUsername(), user.getRole().name());
            log.info("Запрос на логин пришел от пользователя 4 - {}", authenticationRequest.getEmail());
            if(refreshTokensRepository.FindRefreshTokenByUserUUID(userRepository.findByUsername(user.getUsername()).get().getId())!=null){
                refreshTokensRepository.deleteRefreshTokensByUserUUID(userRepository.findByUsername(user.getUsername()).get().getId());
            }
            log.info("Запрос на логин пришел от пользователя 5 - {}", authenticationRequest.getEmail());
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
        String AccessToken = "";
        String RefreshToken = "";
        if(userRepository.findByEmail(registrationRequest.getEmail()).isPresent()){
            return new ResponseEntity("User already exist", HttpStatus.FORBIDDEN);
        }else{
            //Возможно тут подлянка
            User user = new User(registrationRequest.getUsername(), passwordEncoder().encode(registrationRequest.getPassword()),
                    null, registrationRequest.getEmail(),
                    Role.USER, Status.ACTIVE);
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

    @Transactional
    @GetMapping("/api/auth/logout")
    public void logout(@RequestHeader(value = "Authorization") String Authorization, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        log.info("Запрос на выход из приложения ");
        refreshTokensRepository.deleteRefreshTokensByToken(Authorization);
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(httpServletRequest, httpServletResponse, null);
    }

    public String decodeJson(String json, String key){
        Genson genson = new Genson();
        Map<String, String> jsonMap = genson.deserialize(json, Map.class);
        return jsonMap.get(key);
    }
}
