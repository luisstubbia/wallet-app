package org.luisstubbia.walletapp.repository;

import org.luisstubbia.walletapp.model.Movement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovementRepository extends JpaRepository<Movement, Long> {
    @Query(value = "SELECT SUM(amount) FROM movements WHERE balance_uid = :blcUid AND DATE(created_date) = :date ", nativeQuery = true)
    BigDecimal getAccumulateMovementByDateAndBalance(@Param("blcUid") String blcUid, @Param("date") LocalDate date);

    @Query(value = "SELECT id, uid, balance_uid, type, amount, created_date, reference_type, reference_id, reference_date FROM movements WHERE id > :lastId AND balance_uid = :blcUid ", nativeQuery = true)
    List<Movement> findAllGreaterThanLastId(@Param("blcUid") String blcUid, @Param("lastId") Long lastId );

    List<Movement> findAllByBalanceUid(String balanceUid);
}
