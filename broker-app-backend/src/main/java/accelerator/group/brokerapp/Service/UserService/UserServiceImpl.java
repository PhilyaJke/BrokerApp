package accelerator.group.brokerapp.Service.UserService;

import accelerator.group.brokerapp.Entity.User;
import accelerator.group.brokerapp.Repository.UserRepository;
import accelerator.group.brokerapp.Service.UserService.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }
}
