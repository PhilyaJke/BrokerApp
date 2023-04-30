package accelerator.group.brokerapp.Repository;

import accelerator.group.brokerapp.Entity.LastPriceOfSecurities;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LastPriceOfSecuritiesRepository extends CrudRepository<LastPriceOfSecurities, String> {
}
