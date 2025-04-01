package org.luisstubbia.walletapp.service;

import jakarta.transaction.Transactional;
import org.luisstubbia.walletapp.model.AccountBalanceProjection;
import org.luisstubbia.walletapp.model.Movement;
import org.luisstubbia.walletapp.repository.AccountBalanceRepository;
import org.luisstubbia.walletapp.repository.MovementRepository;
import org.luisstubbia.walletapp.repository.ProjectionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class ProjectionService {

    private final AccountBalanceRepository accountBalanceRepository;
    private final MovementRepository movementRepository;
    private final Queue<Object> projectionQueue;
    private final ProjectionRepository projectionRepository;

    public ProjectionService(AccountBalanceRepository accountBalanceRepository, MovementRepository movementRepository, ProjectionRepository projectionRepository) {
        this.accountBalanceRepository = accountBalanceRepository;
        this.movementRepository = movementRepository;
        this.projectionQueue = new ConcurrentLinkedQueue<>();
        this.projectionRepository = projectionRepository;
    }

    @Scheduled(cron = "0 0/10 * * * ?")
    public void queueProjections() {
        projectionQueue.clear();
        accountBalanceRepository.findAll().forEach(accountBalance -> {
            System.out.printf("Queueing projection %s\n", accountBalance.getUid());
            projectionQueue.add(accountBalance.getUid());
        });
    }

    public void forceSync(List<String> blcList) {
        projectionQueue.addAll(blcList);
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void calculateProjection() {
        Object obj = projectionQueue.poll();
        if (obj != null) {
            var balanceUid = (String) obj;
            System.out.printf("calculate projection for: %s\n", obj);
            updateProjection(balanceUid);
        }
    }

    public void updateProjection(String balanceUid) {
        var prj = projectionRepository.findByBalanceUid(balanceUid);
        if (prj == null) {
            System.out.printf("Creating projection for balance uid: %s\n", balanceUid);
            var mvList = movementRepository.findAllByBalanceUid(balanceUid);
            if (mvList.isEmpty()) {
                return;
            }
            BigDecimal result = mvList.stream().map(Movement::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            Optional<Movement> maxMovId = mvList.stream().max(Comparator.comparing(Movement::getId));
            projectionRepository.optimisticLockingPersist(balanceUid, result, maxMovId.get().getId(), LocalDateTime.now(), 0L);
            return;
        }
        System.out.printf("updating projection for balance uid: %s\n", balanceUid);
        var mvList = movementRepository.findAllGreaterThanLastId(balanceUid, prj.getLastMovementId());
        if (!mvList.isEmpty()) {
            BigDecimal result = mvList.stream().map(Movement::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            Optional<Movement> maxMovId = mvList.stream().max(Comparator.comparing(Movement::getId));
            projectionRepository.optimisticLockingUpdate(balanceUid, result, maxMovId.get().getId(), LocalDateTime.now(), prj.getVersion());
        }
    }

    public List<AccountBalanceProjection> getProjections(List<String> balanceUids) {
        return projectionRepository.findAllByBalanceUid(balanceUids);
    }

    public int getQueueSize() {
        return projectionQueue.size();
    }
}