package org.luisstubbia.walletapp.service;

import org.luisstubbia.walletapp.model.Deposit;
import org.luisstubbia.walletapp.repository.DepositRepository;
import org.luisstubbia.walletapp.service.strategy.MovementStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepositService {
    @Qualifier("DEPOSIT")
    private final MovementStrategy<Deposit> movementStrategy;
    private final DepositRepository depositRepository;

    public DepositService(MovementStrategy<Deposit> movementStrategy, DepositRepository depositRepository) {
        this.movementStrategy = movementStrategy;
        this.depositRepository = depositRepository;
    }

    public Deposit createDeposit(Deposit deposit) {
        var dp = depositRepository.save(deposit);
        movementStrategy.applyMovements(dp);
        return dp;
    }

    public List<Deposit> getDeposits(Long accountId) {
        return depositRepository.findAllByAccountId(accountId);
    }
}
