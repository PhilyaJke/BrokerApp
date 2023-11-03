package accelerator.group.brokerapp.Rest;

import accelerator.group.brokerapp.Entity.User;
import accelerator.group.brokerapp.Requests.AuthenticationRequest;
import accelerator.group.brokerapp.Requests.RegistrationRequest;
import accelerator.group.brokerapp.Security.JwtTokenProvider;
import accelerator.group.brokerapp.Service.MVCService.UserService.UserMVCServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@Slf4j
public class UserRestApi {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserMVCServiceImpl userMVCServiceImpl;


    @Autowired
    public UserRestApi(@Qualifier("UserMVCService") UserMVCServiceImpl userMVCServiceImpl,
                       JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
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

        try {
            JSONObject jsonObject = userMVCServiceImpl.login(authenticationRequest, user);
            log.info("Логин прошел успешно, пользователь - {}", authenticationRequest.getEmail());
            return new ResponseEntity(jsonObject, HttpStatus.OK);
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

        if(userMVCServiceImpl.findByEmail(registrationRequest.getEmail()).isPresent()){
            return new ResponseEntity("User already exist", HttpStatus.FORBIDDEN);
        }else{
            JSONObject jsonObject = userMVCServiceImpl.registration(registrationRequest);
            log.info("Регистрация прошла успешно, пользователь - {}", registrationRequest.getEmail());
            return new ResponseEntity(jsonObject, HttpStatus.CREATED);
        }
    }

    @GetMapping("/api/auth/updateaccesstoken")
    public ResponseEntity updateAccessToken(@RequestHeader(value = "Authorization") String Authorization){
        log.info("Запрос на обновление access токена");
        User user = userMVCServiceImpl.findByUsername(jwtTokenProvider.getUsername(Authorization))
                .orElseThrow(() -> new UsernameNotFoundException("Wrong auth key"));
        JSONObject jsonObject = userMVCServiceImpl.updateAccessToken(Authorization, user);
        log.info("Access token successfully updated");
        return new ResponseEntity(jsonObject, HttpStatus.CREATED);
    }

    //Не работает - доделать
    @Transactional
    @GetMapping("/api/auth/logout")
    public void logout(@RequestHeader(value = "Authorization") String Authorization, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        log.info("Запрос на выход из приложения ");
        userMVCServiceImpl.logout(Authorization, httpServletRequest, httpServletResponse);
    }

    // TODO: пересмотреть, переделать - не работает
    @GetMapping("/api/profile")
    public ResponseEntity userProfile(@RequestHeader(value = "Authorization") String Authorization){
        User user = userMVCServiceImpl.findByUsername(jwtTokenProvider.getUsername(Authorization))
                .orElseThrow(() -> new UsernameNotFoundException("User doesn't exist"));
        JSONObject jsonObject = userMVCServiceImpl.userProfile(Authorization, user);
        return ResponseEntity.ok(jsonObject);
    }
}
