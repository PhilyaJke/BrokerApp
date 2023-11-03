package accelerator.group.brokerapp.Service.DAOService.LastPriceOfSecurityService;

import accelerator.group.brokerapp.Entity.LastPriceOfSecurities;

import java.util.Optional;

public interface LastPriceOfSecurityDAOService {

    Optional<LastPriceOfSecurities> findLastPriceOfSecurity(String figi);

    boolean existById(String id);

    void save(LastPriceOfSecurities lastPriceOfSecurities);

    Optional<LastPriceOfSecurities> findById(String figi);

    void deleteById(String figi);

}
