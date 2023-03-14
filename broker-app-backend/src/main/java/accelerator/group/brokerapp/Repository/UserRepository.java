package accelerator.group.brokerapp.Repository;

import accelerator.group.brokerapp.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("SELECT u FROM User u WHERE u.id = ?1")
    User findUsersById(@Param(value = "id") String id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
