package org.luisstubbia.walletapp.service.strategy;

import org.luisstubbia.walletapp.model.*;
import org.luisstubbia.walletapp.repository.MovementRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("WITHDRAW")
public class WithdrawalStrategy extends MovementStrategy<Withdraw> {
    protected WithdrawalStrategy(MovementRepository movementRepository) {
        super(movementRepository);
    }

    @Override
    public List<Movement> impactAmount(Withdraw tx) {
        List<Movement> withdrawalList = new ArrayList<>();
        Movement depMv = Movement.builder()
                .amount(tx.getAmount().negate())
                .type(MovementTypeEnum.DEBIT)
                .balanceUid(getBalanceUid(tx.getAccountId(), tx.getCurrency()))
                .referenceDate(tx.getCreatedDate())
                .referenceId(tx.getId())
                .referenceType(TransactionTypeEnum.WITHDRAW)
                .uid(getUid(MovementTypeEnum.DEBIT.name(), tx.getId(), tx.getAccountId()))
                .build();
        withdrawalList.add(depMv);
        return withdrawalList;
    }

    @Override
    public List<Movement> impactFee(Withdraw tx){
        // Must be implemented
        return List.of();
    }

    private String getBalanceUid(Long accountId, String currency) {
        return String.format("%s-%s-%s", BalancePartitionTypeEnum.AVAILABLE.name(), currency.toUpperCase(), accountId);
    }

    private String getUid(String type, Long id, Long accountId) {
        return String.format("%s-%s-%s-%s",TransactionTypeEnum.WITHDRAW.name(), id, accountId, type);
    }
}
