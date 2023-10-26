package accelerator.group.brokerapp.Service.MVCService.RefreshTokensService;

import accelerator.group.brokerapp.Repository.RefreshTokensRepository;
import accelerator.group.brokerapp.Service.DAOService.RefreshTokensService.RefreshTokensDAOServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokensMVCServiceImpl extends RefreshTokensDAOServiceImpl implements RefreshTokensMVCService {
    @Autowired
    public RefreshTokensMVCServiceImpl(RefreshTokensRepository refreshTokensRepository) {
        super(refreshTokensRepository);
    }
}
