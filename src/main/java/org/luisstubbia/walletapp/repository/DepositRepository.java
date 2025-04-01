package org.luisstubbia.walletapp.repository;

import org.luisstubbia.walletapp.model.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, Long> {
    List<Deposit> findAllByAccountId(Long accountId);
}
