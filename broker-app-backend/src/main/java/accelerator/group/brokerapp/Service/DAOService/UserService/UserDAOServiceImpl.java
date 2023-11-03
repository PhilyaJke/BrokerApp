package accelerator.group.brokerapp.Service.DAOService.UserService;

import accelerator.group.brokerapp.Entity.User;
import accelerator.group.brokerapp.Repository.UserRepository;
import accelerator.group.brokerapp.Security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service("UserDAOService")
public class UserDAOServiceImpl implements UserDAOService {

    private final UserRepository userRepository;

    @Autowired
    public UserDAOServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User userProfileInfo(String username) {
        return userRepository.UserProfileInfo(username);
    }
}
