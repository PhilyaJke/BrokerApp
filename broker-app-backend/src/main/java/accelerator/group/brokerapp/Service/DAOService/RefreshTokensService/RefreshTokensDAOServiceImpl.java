package accelerator.group.brokerapp.Service.DAOService.RefreshTokensService;

import accelerator.group.brokerapp.Entity.RefreshTokens;
import accelerator.group.brokerapp.Repository.RefreshTokensRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RefreshTokensDAOServiceImpl implements RefreshTokensDAOService{

    private final RefreshTokensRepository refreshTokensRepository;

    @Autowired
    public RefreshTokensDAOServiceImpl(RefreshTokensRepository refreshTokensRepository) {
        this.refreshTokensRepository = refreshTokensRepository;
    }

    @Override
    public String FindRefreshTokenByUserUUID(UUID uuid) {
        return refreshTokensRepository.FindRefreshTokenByUserUUID(uuid);
    }

    @Override
    public void deleteRefreshTokensByUserUUID(UUID uuid) {
        refreshTokensRepository.deleteRefreshTokensByUserUUID(uuid);
    }

    @Override
    public void saveRefreshToken(UUID uuid, String token) {
        refreshTokensRepository.save(saveUserSecurityToken(uuid, token));
    }

    @Override
    public void deleteRefreshTokensByToken(String token) {
        refreshTokensRepository.deleteRefreshTokensByToken(token);
    }

    public static RefreshTokens saveUserSecurityToken(UUID uuid, String token){
        RefreshTokens refreshTokens = new RefreshTokens();
        refreshTokens.setUserUUID(uuid);
        refreshTokens.setToken(token);
        return refreshTokens;
    }
}
