package accelerator.group.brokerapp.Service.DAOService.RefreshTokensService;

import accelerator.group.brokerapp.Entity.RefreshTokens;

import java.util.UUID;

public interface RefreshTokensDAOService {
    String FindRefreshTokenByUserUUID(UUID uuid);

    void deleteRefreshTokensByUserUUID(UUID uuid);

    void saveRefreshToken(UUID uuid, String token);

    void deleteRefreshTokensByToken(String token);
}
