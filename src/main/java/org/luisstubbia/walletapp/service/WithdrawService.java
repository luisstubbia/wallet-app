package org.luisstubbia.walletapp.service;

import org.luisstubbia.walletapp.model.Withdraw;
import org.luisstubbia.walletapp.repository.WithdrawRepository;
import org.luisstubbia.walletapp.service.strategy.MovementStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WithdrawService {
    @Qualifier("WITHDRAW")
    private final MovementStrategy<Withdraw> movementStrategy;
    private final WithdrawRepository withdrawRepository;

    public WithdrawService(MovementStrategy<Withdraw> movementStrategy, WithdrawRepository withdrawRepository) {
        this.movementStrategy = movementStrategy;
        this.withdrawRepository = withdrawRepository;
    }

    public Withdraw createWithdraw(Withdraw withdraw) {
        var wdw = withdrawRepository.save(withdraw);
        movementStrategy.applyMovements(wdw);
        return wdw;
    }

    public List<Withdraw> getWithdrawals(Long accountId) {
        return withdrawRepository.findAllByAccountId(accountId);
    }
}
