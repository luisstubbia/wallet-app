package org.luisstubbia.walletapp.service;

import org.luisstubbia.walletapp.exception.ConflictEntityException;
import org.luisstubbia.walletapp.model.Account;
import org.luisstubbia.walletapp.repository.AccountRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account getAccountByUser(Long userId) throws AccountNotFoundException {
        return accountRepository.findByUserId(userId).orElseThrow(() -> new AccountNotFoundException(String.format("account for userId: %d not found", userId)));
    }

    public Account getAccountById(Long id) throws AccountNotFoundException {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(String.format("account id %d not found", id)));
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account createAccount(Account account) throws ConflictEntityException {
        account.setCreatedDate(LocalDateTime.now());
        try {
            account = accountRepository.save(account);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictEntityException(String.format("Account for userId %s already exists", account.getUserId()));
        }
        return account;
    }
}
