package accelerator.group.brokerapp.Repository;

import accelerator.group.brokerapp.Entity.Securities;
import accelerator.group.brokerapp.Responses.SecuritiesResponses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SecuritiesRepository extends JpaRepository<Securities, Long> {

    @Query("SELECT s.Figi FROM Securities s")
    List<String> findAllFigiSecurities();

    @Query("SELECT s FROM Securities s WHERE s.region <> 'RU'")
    List<Securities> findAllForeignSecurities();

    @Query("SELECT s FROM Securities s WHERE s.region = 'RU'")
    List<Securities> findAllRuSecurities();

    @Query(value = "SELECT s FROM Securities s WHERE s.region = ?1")
    List<Securities> findForeignSecurities(@Param(value = "country") String country);

    @Query(value = "SELECT s FROM Securities s WHERE s.sector = ?1")
    List<Securities> findSecuritiesBySector(@Param(value = "sector") String sector);

}
