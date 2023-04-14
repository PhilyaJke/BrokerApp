package accelerator.group.brokerapp.Repository;

import accelerator.group.brokerapp.Entity.RefreshTokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefreshTokensRepository extends JpaRepository<RefreshTokens, Long> {

    @Modifying
    @Query("DELETE FROM RefreshTokens r WHERE r.userUUID = ?1")
    void deleteRefreshTokensByUserUUID(@Param(value = "userUUID")UUID userUUID);

    @Query("select r.token FROM RefreshTokens r WHERE r.userUUID = ?1")
    String FindRefreshTokenByUserUUID(@Param(value = "userUUID") UUID userUUID);

    @Modifying
    @Query("DELETE FROM RefreshTokens r WHERE r.token = ?1")
    void deleteRefreshTokensByToken(@Param(value = "token") String token);
}
