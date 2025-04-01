package org.luisstubbia.walletapp.repository;

import org.luisstubbia.walletapp.model.AccountBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountBalanceRepository extends JpaRepository<AccountBalance, Long> {
    List<AccountBalance> findByAccountId(Long accountId);
}
