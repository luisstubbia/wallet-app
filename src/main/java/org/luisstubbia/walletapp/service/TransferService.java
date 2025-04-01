package org.luisstubbia.walletapp.service;

import org.luisstubbia.walletapp.model.Transfer;
import org.luisstubbia.walletapp.repository.TransferRepository;
import org.luisstubbia.walletapp.service.strategy.MovementStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransferService {
    @Qualifier("TRANSFER")
    private final MovementStrategy<Transfer> movementStrategy;
    private final TransferRepository transferRepository;
    private final AccountService accountService;

    public TransferService(MovementStrategy<Transfer> movementStrategy, TransferRepository transferRepository, AccountService accountService) {
        this.movementStrategy = movementStrategy;
        this.transferRepository = transferRepository;
        this.accountService = accountService;
    }

    public Transfer createTransfer(Transfer transfer) {
        if (transfer.getToAccountId().equals(transfer.getFromAccountId())) {
            throw new IllegalArgumentException("Transfer can't be the same account");
        }
        if (transfer.getAmount().compareTo(BigDecimal.ZERO) <= 0 ){
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        try {
            accountService.getAccountById(transfer.getToAccountId());
        } catch (AccountNotFoundException e) {
            throw new IllegalArgumentException("Account TO not found for account id " + transfer.getToAccountId());
        }
        var trf = transferRepository.save(transfer);
        movementStrategy.applyMovements(trf);
        return trf;
    }

    public List<Transfer> getTransfers(Long accountId) {
        List<Transfer> transfers = transferRepository.findAllByFromAccountId(accountId);
        transfers.addAll(transferRepository.findAllByToAccountId(accountId));
        return transfers;
    }
}
