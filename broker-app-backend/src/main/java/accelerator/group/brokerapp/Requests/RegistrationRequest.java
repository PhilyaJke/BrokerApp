package accelerator.group.brokerapp.Requests;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Component
public class RegistrationRequest {

    private String username;
    private String email;
    private String password;

}
