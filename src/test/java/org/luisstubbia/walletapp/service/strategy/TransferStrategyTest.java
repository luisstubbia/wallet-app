package org.luisstubbia.walletapp.service.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.luisstubbia.walletapp.model.*;
import org.luisstubbia.walletapp.repository.MovementRepository;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransferStrategyTest {

    @Mock
    private MovementRepository movementRepository;

    private TransferStrategy transferStrategy;

    private Transfer testTransfer;

    @BeforeEach
    void setUp() {
        transferStrategy = new TransferStrategy(movementRepository);

        testTransfer = Transfer.builder()
                .Id(1L)
                .fromAccountId(100L)
                .toAccountId(200L)
                .amount(new BigDecimal("500.00"))
                .currency("USD")
                .createdDate(LocalDateTime.now())
                .build();
    }

    @Test
    void impactAmount_ShouldCreateTwoMovements() {
        List<Movement> result = transferStrategy.impactAmount(testTransfer);
        assertEquals(2, result.size(), "Should create 2 movements (debit and credit)");

        Movement debitMovement = result.get(0);
        assertEquals(new BigDecimal("-500.00"), debitMovement.getAmount());
        assertEquals(MovementTypeEnum.DEBIT, debitMovement.getType());
        assertEquals("AVAILABLE-USD-100", debitMovement.getBalanceUid());
        assertEquals(TransactionTypeEnum.TRANSFER, debitMovement.getReferenceType());
        assertEquals("TRANSFER-1-100-DEBIT", debitMovement.getUid());

        Movement creditMovement = result.get(1);
        assertEquals(new BigDecimal("500.00"), creditMovement.getAmount());
        assertEquals(MovementTypeEnum.CREDIT, creditMovement.getType());
        assertEquals("AVAILABLE-USD-200", creditMovement.getBalanceUid());
        assertEquals(TransactionTypeEnum.TRANSFER, creditMovement.getReferenceType());
        assertEquals("TRANSFER-1-200-CREDIT", creditMovement.getUid());
    }

    @Test
    void impactAmount_ShouldHandleDifferentCurrency() {
        testTransfer.setCurrency("EUR");
        List<Movement> result = transferStrategy.impactAmount(testTransfer);
        assertEquals("AVAILABLE-EUR-100", result.get(0).getBalanceUid());
        assertEquals("AVAILABLE-EUR-200", result.get(1).getBalanceUid());
    }

    @Test
    void impactFee_ShouldReturnEmptyList() {
        List<Movement> result = transferStrategy.impactFee(testTransfer);
        assertTrue(result.isEmpty(), "impactFee should return empty list by default");
    }

    @Test
    void getBalanceUid_ShouldFormatCorrectly() {
        String result = transferStrategy.getBalanceUid(123L, "GBP");
        assertEquals("AVAILABLE-GBP-123", result);
    }

    @Test
    void getUid_ShouldFormatCorrectly() {
        String result = transferStrategy.getUid("DEBIT", 456L, 789L);
        assertEquals("TRANSFER-456-789-DEBIT", result);
    }
}