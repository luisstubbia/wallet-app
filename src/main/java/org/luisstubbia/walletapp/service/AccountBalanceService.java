package org.luisstubbia.walletapp.service;

import org.luisstubbia.walletapp.model.AccountBalance;
import org.luisstubbia.walletapp.repository.AccountBalanceRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountBalanceService {
    private final AccountBalanceRepository accountBalanceRepository;
    public AccountBalanceService(AccountBalanceRepository accountBalanceRepository) {
        this.accountBalanceRepository = accountBalanceRepository;
    }

    public void createAccountBalance(AccountBalance accountBalance) {
        accountBalance.setUid(accountBalance.formatUid());
        accountBalanceRepository.save(accountBalance);
    }
}
