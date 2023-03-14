package accelerator.group.brokerapp.Requests;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class AuthenticationRequest {

    private String email;
    private String password;

}
