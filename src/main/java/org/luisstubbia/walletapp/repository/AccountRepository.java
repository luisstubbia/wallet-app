package org.luisstubbia.walletapp.repository;

import org.luisstubbia.walletapp.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUserId(Long id);
}
