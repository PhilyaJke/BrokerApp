package accelerator.group.brokerapp.Repository;

import accelerator.group.brokerapp.Entity.AdditionalStocksInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdditionalStocksInfoRepository extends JpaRepository<AdditionalStocksInfo, Long> {

}
