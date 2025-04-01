package org.luisstubbia.walletapp.service.strategy;

import org.luisstubbia.walletapp.model.*;
import org.luisstubbia.walletapp.repository.MovementRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("TRANSFER")
public class TransferStrategy extends MovementStrategy<Transfer> {
    protected TransferStrategy(MovementRepository movementRepository) {
        super(movementRepository);
    }

    @Override
    public List<Movement> impactAmount(Transfer tx) {
        List<Movement> transferList = new ArrayList<>();
        Movement originMvm = Movement.builder()
                .amount(tx.getAmount().negate())
                .type(MovementTypeEnum.DEBIT)
                .balanceUid(getBalanceUid(tx.getFromAccountId(), tx.getCurrency()))
                .referenceDate(tx.getCreatedDate())
                .referenceId(tx.getId())
                .referenceType(TransactionTypeEnum.TRANSFER)
                .uid(getUid(MovementTypeEnum.DEBIT.name(), tx.getId(), tx.getFromAccountId()))
                .build();
        transferList.add(originMvm);
        Movement destMvm = Movement.builder()
                .amount(tx.getAmount())
                .type(MovementTypeEnum.CREDIT)
                .balanceUid(getBalanceUid(tx.getToAccountId(), tx.getCurrency()))
                .referenceDate(tx.getCreatedDate())
                .referenceId(tx.getId())
                .referenceType(TransactionTypeEnum.TRANSFER)
                .uid(getUid(MovementTypeEnum.CREDIT.name(), tx.getId(), tx.getToAccountId()))
                .build();
        transferList.add(destMvm);
        return transferList;
    }

    @Override
    public List<Movement> impactFee(Transfer tx){
        // Must be implemented
        return List.of();
    }

    public String getBalanceUid(Long accountId, String currency) {
        return String.format("%s-%s-%s", BalancePartitionTypeEnum.AVAILABLE.name(), currency.toUpperCase(), accountId);
    }

    public String getUid(String type, Long id, Long accountId) {
        return String.format("%s-%s-%s-%s",TransactionTypeEnum.TRANSFER.name(), id, accountId, type);
    }
}