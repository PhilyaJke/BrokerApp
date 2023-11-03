package accelerator.group.brokerapp.Service.DAOService.UserService;

import accelerator.group.brokerapp.Entity.User;
import accelerator.group.brokerapp.Security.JwtTokenProvider;

import java.util.Optional;

public interface UserDAOService {

    void saveUser(User user);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    User userProfileInfo(String username);

}
