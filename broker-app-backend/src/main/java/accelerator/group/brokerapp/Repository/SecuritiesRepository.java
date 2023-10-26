package accelerator.group.brokerapp.Repository;

import accelerator.group.brokerapp.Entity.Securities;
import accelerator.group.brokerapp.Responses.SecuritiesFullInfoResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SecuritiesRepository extends JpaRepository<Securities, Long> {

    @Query("SELECT s.Figi FROM Securities s")
    List<String> findAllFigiSecurities();

    @Query("SELECT s FROM Securities s WHERE s.Figi = ?1")
    Securities findSecurityByFigi(@Param(value = "figi") String figi);

    @Query(value = "SELECT s.Figi FROM Securities s")
    List<String> findLimitedSecurities(Pageable pageable);

    @Query(
            nativeQuery = true,
            name = "FindRUStocksInfo",
            countName = "CountSecurities")
    List<SecuritiesFullInfoResponse> findAllRuSecuritiesPage(Pageable pageable);

    @Query(
            nativeQuery = true,
            name = "FindForeignStocksInfo",
            countName = "CountSecurities")
    List<SecuritiesFullInfoResponse> findAllForeignSecuritiesPage(Pageable pageable);

    @Query(
            nativeQuery = true,
            name = "FindFullStocksInfo",
            countName = "CountSecurities")
    List<SecuritiesFullInfoResponse> findAllSecuritiesPage(Pageable pageable);

    @Query(
            nativeQuery = true,
            name = "FindUsersSecurities"
    )
    SecuritiesFullInfoResponse findUsersSecurities();

    @Query(value = "SELECT s FROM Securities s WHERE s.Ticker = ?1")
    Optional<Securities> findSecuritiesByTicker(@Param(value = "ticker") String ticker);

    @Query(value = "SELECT s.Figi FROM Securities s WHERE s.Ticker = ?1")
    Optional<String> findFigiByTicker(@Param(value = "ticker") String ticker);

}
