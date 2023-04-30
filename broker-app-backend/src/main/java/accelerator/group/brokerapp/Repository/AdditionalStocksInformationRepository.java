package accelerator.group.brokerapp.Repository;

import accelerator.group.brokerapp.Entity.AdditionalStocksInformation;
import accelerator.group.brokerapp.Entity.Securities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdditionalStocksInformationRepository extends JpaRepository<AdditionalStocksInformation, Long> {

    @Query("SELECT a From AdditionalStocksInformation a WHERE a.securities.id = ?1")
    AdditionalStocksInformation findAddStocksInfoById(@Param(value = "id") long id);
}
