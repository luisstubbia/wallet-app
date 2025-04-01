package org.luisstubbia.walletapp.repository;

import org.luisstubbia.walletapp.model.AccountBalanceProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProjectionRepository extends JpaRepository<AccountBalanceProjection, Long> {
    AccountBalanceProjection findByBalanceUid(String balanceUid);

    @Query(value = "SELECT balance_uid, amount, last_movement_id, created_date, updated_date, version FROM balance_projection WHERE balance_uid in (:blcUids) ", nativeQuery = true)
    List<AccountBalanceProjection> findAllByBalanceUid(@Param("blcUids") List<String> blcUids);

    @Modifying
    @Query(value = "INSERT INTO balance_projection (balance_uid, amount, last_movement_id, created_date, updated_date, version) " +
            "VALUES(:blcUid, :amount, :lastId, :created, :created, :version) ON DUPLICATE KEY UPDATE amount = amount + :amount, version = version + 1 ", nativeQuery = true)
    void optimisticLockingPersist(@Param("blcUid") String blcUid, @Param("amount") BigDecimal amount,
                                  @Param("lastId") Long lastId, @Param("created") LocalDateTime created, @Param("version") Long version);

    @Modifying
    @Query(value = "UPDATE balance_projection SET amount = amount + :amount, last_movement_id = :lastId, updated_date = :updated, version = version + 1 " +
            "WHERE balance_uid = :blcUid AND version = :version ", nativeQuery = true)
    void optimisticLockingUpdate(@Param("blcUid") String blcUid, @Param("amount") BigDecimal amount,
                                 @Param("lastId") Long lastId, @Param("updated") LocalDateTime updated, @Param("version") Long version);
}
