package org.luisstubbia.walletapp.repository;

import org.luisstubbia.walletapp.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
    List<Transfer> findAllByFromAccountId(Long accountId);
    List<Transfer> findAllByToAccountId(Long accountId);
}
