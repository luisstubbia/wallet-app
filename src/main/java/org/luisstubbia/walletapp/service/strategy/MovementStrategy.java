package org.luisstubbia.walletapp.service.strategy;

import jakarta.transaction.Transactional;
import org.luisstubbia.walletapp.model.Movement;
import org.luisstubbia.walletapp.repository.MovementRepository;
import org.luisstubbia.walletapp.service.ProjectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public abstract class MovementStrategy<T> {
    @Autowired
    private ProjectionService projectionService;

    private final MovementRepository movementRepository;

    protected MovementStrategy(MovementRepository movementRepository) {
        this.movementRepository = movementRepository;
    }

    @Transactional
    public void applyMovements(T tx) {
        List<Movement> finalMovements = this.impactAmount(tx);
        finalMovements.addAll(this.impactFee(tx));
        movementRepository.saveAll(finalMovements);
        List<String> blcList = finalMovements.stream().map(Movement::getBalanceUid).toList();
        projectionService.forceSync(blcList);
    }

    protected abstract List<Movement> impactAmount(T tx);

    protected abstract List<Movement> impactFee(T tx);
}
