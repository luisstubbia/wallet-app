package org.luisstubbia.walletapp.repository;

import org.luisstubbia.walletapp.model.MovementSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SnapshotRepository extends JpaRepository<MovementSnapshot, Long> {
    @Query(value = "SELECT id, uid, balance_uid, amount, created_date FROM movement_snapshots WHERE balance_uid = :blcUid ORDER BY id DESC LIMIT 1", nativeQuery = true)
    MovementSnapshot findFirstByOrderByIdDesc(@Param("blcUid") String blcUid);

    @Query(value = "SELECT id, uid, balance_uid, amount, created_date FROM movement_snapshots WHERE balance_uid in (:blcUids) AND created_date = :date ", nativeQuery = true)
    List<MovementSnapshot> findAllByBalanceUid(@Param("blcUids") List<String> blcUids, @Param("date") LocalDate date);
}
