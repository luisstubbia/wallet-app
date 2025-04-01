package org.luisstubbia.walletapp.service;

import jakarta.transaction.Transactional;
import lombok.Getter;
import org.luisstubbia.walletapp.model.MovementSnapshot;
import org.luisstubbia.walletapp.repository.AccountBalanceRepository;
import org.luisstubbia.walletapp.repository.MovementRepository;
import org.luisstubbia.walletapp.repository.SnapshotRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class SnapshotService {

    private final AccountBalanceRepository accountBalanceRepository;
    private final MovementRepository movementRepository;
    private final SnapshotRepository snapshotRepository;
    @Getter
    private final Queue<Object> snapshotQueue;

    public SnapshotService(AccountBalanceRepository accountBalanceRepository, MovementRepository movementRepository, SnapshotRepository snapshotRepository) {
        this.accountBalanceRepository = accountBalanceRepository;
        this.movementRepository = movementRepository;
        this.snapshotRepository = snapshotRepository;
        this.snapshotQueue = new ConcurrentLinkedQueue<>();
    }

    @Scheduled(cron = "0 10 0 * * ?")
    public void queueSnapshots() {
        snapshotQueue.clear();
        accountBalanceRepository.findAll().forEach(accountBalance -> {
            System.out.printf("Queueing snapshot %s\n", accountBalance);
            snapshotQueue.add(accountBalance.getUid());
        });
    }

    @Async("AsyncExecutor")
    @Transactional
    public void calculateSnapshots() {
        Object obj = snapshotQueue.poll();
        if (obj != null) {
            var balanceUid = (String) obj;
            System.out.printf("Calculating snapshots %s\n", balanceUid);
            processSnapshot(balanceUid);
        }
    }

    public void processSnapshot(String balanceUid) {
        var previousDate = LocalDate.now().minusDays(1);
        var amount = movementRepository.getAccumulateMovementByDateAndBalance(balanceUid, previousDate);
        System.out.printf("Adding snapshots for %s from %s amount %s", balanceUid, previousDate, amount);
        if (amount != null) {
            var ms = MovementSnapshot.builder()
                    .balanceUid(balanceUid)
                    .amount(amount)
                    .createdDate(previousDate)
                    .uid(balanceUid + previousDate)
                    .build();
            snapshotRepository.save(ms);
        }
    }

    public List<MovementSnapshot> getSnapshots(List<String> blcUids, LocalDate date) {
        return snapshotRepository.findAllByBalanceUid(blcUids, date);
    }
}