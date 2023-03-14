package accelerator.group.brokerapp.Service.SecuritiesService;

import accelerator.group.brokerapp.Entity.Securities;
import accelerator.group.brokerapp.Repository.SecuritiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecuritiesServiceImpl implements SecuritiesService{

    private final SecuritiesRepository securitiesRepository;

    @Autowired
    public SecuritiesServiceImpl(SecuritiesRepository securitiesRepository) {
        this.securitiesRepository = securitiesRepository;
    }


    @Override
    public List<Securities> findAllSecurities() {
        return securitiesRepository.findAll();
    }

    @Override
    public List<Securities> findAllForeignSecurities() {
        return securitiesRepository.findAllForeignSecurities();
    }

    @Override
    public List<Securities> findAllRuSecurities() {
        return securitiesRepository.findAllRuSecurities();
    }

    @Override
    public List findForeignSecurities(String county) {
        return securitiesRepository.findForeignSecurities(county);
    }

    @Override
    public List<Securities> findSecuritiesBySector(String sector) {
        return securitiesRepository.findSecuritiesBySector(sector);
    }

}
