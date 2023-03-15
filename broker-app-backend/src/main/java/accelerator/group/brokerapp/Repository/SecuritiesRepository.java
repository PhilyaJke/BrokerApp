package accelerator.group.brokerapp.Repository;

import accelerator.group.brokerapp.Entity.Securities;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecuritiesRepository extends JpaRepository<Securities, Long> {

    @Query("SELECT s.Figi FROM Securities s")
    List<String> findAllFigiSecurities();

    @Query("SELECT s FROM Securities s WHERE s.region <> 'RU'")
    Page<Securities> findAllForeignSecuritiesPage(org.springframework.data.domain.Pageable pageable);

    @Query("SELECT s FROM Securities s WHERE s.region = 'RU'")
    Page<Securities> findAllRuSecuritiesPage(Pageable pageable);

    @Query("SELECT s FROM Securities s")
    Page<Securities> findAllSecurities(org.springframework.data.domain.Pageable pageable);

    @Query(value = "SELECT s FROM Securities s WHERE s.region = ?1")
    List<Securities> findForeignSecurities(@Param(value = "country") String country);

    @Query(value = "SELECT s FROM Securities s WHERE s.sector = ?1")
    List<Securities> findSecuritiesBySector(@Param(value = "sector") String sector);

}
