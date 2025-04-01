package org.luisstubbia.walletapp.service.strategy;

import org.luisstubbia.walletapp.model.*;
import org.luisstubbia.walletapp.repository.MovementRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("DEPOSIT")
public class DepositStrategy extends MovementStrategy<Deposit> {
    protected DepositStrategy(MovementRepository movementRepository) {
        super(movementRepository);
    }

    @Override
    public List<Movement> impactAmount(Deposit tx) {
        List<Movement> depositList = new ArrayList<>();
        Movement depMv = Movement.builder()
                .amount(tx.getAmount())
                .type(MovementTypeEnum.CREDIT)
                .balanceUid(getBalanceUid(tx.getAccountId(), tx.getCurrency()))
                .referenceDate(tx.getCreatedDate())
                .referenceId(tx.getId())
                .referenceType(TransactionTypeEnum.DEPOSIT)
                .uid(getUid(MovementTypeEnum.CREDIT.name(), tx.getId(), tx.getAccountId()))
                .build();
        depositList.add(depMv);
        return depositList;
    }

    @Override
    public List<Movement> impactFee(Deposit tx){
        // Must be implemented
        return List.of();
    }

    private String getBalanceUid(Long accountId, String currency) {
        return String.format("%s-%s-%s", BalancePartitionTypeEnum.AVAILABLE.name(), currency.toUpperCase(), accountId);
    }

    private String getUid(String type, Long id, Long accountId) {
        return String.format("%s-%s-%s-%s",TransactionTypeEnum.DEPOSIT.name(), id, accountId, type);
    }
}
