package accelerator.group.brokerapp.Repository;

import accelerator.group.brokerapp.Entity.Securities;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecuritiesRepository extends JpaRepository<Securities, Long> {

    @Query("SELECT s.Figi FROM Securities s")
    List<String> findAllFigiSecurities();

    @Query("SELECT s FROM Securities s WHERE s.Figi = ?1")
    Securities findSecurityByFigi(@Param(value = "figi") String figi);

    @Modifying
    @Query("UPDATE Securities s SET s.Lastprice = ?1 WHERE s.Figi = ?2")
    void updateLastPrices(@Param(value = "lastPrice") String lastPrice, @Param(value = "figi") String figi);

    @Query(value = "SELECT s.figi FROM Securities s", nativeQuery = true)
    Page<String> findLimitedSecurities(Pageable pageable);




    @Query("SELECT s FROM Securities s WHERE s.region = 'RU'")
    Page<Securities> findAllRuSecuritiesPage(Pageable pageable);

    @Query("SELECT s FROM Securities s WHERE s.region <> 'RU'")
    Page<Securities> findAllForeignSecuritiesPage(Pageable pageable);

    @Query("SELECT s FROM Securities s ")
    Page<Securities> findAllSecuritiesPage(Pageable pageable);


}
