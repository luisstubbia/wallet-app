package org.luisstubbia.walletapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.luisstubbia.walletapp.model.AccountBalance;
import org.luisstubbia.walletapp.model.BalancePartitionTypeEnum;
import org.luisstubbia.walletapp.model.Movement;
import org.luisstubbia.walletapp.model.MovementSnapshot;
import org.luisstubbia.walletapp.repository.AccountBalanceRepository;
import org.luisstubbia.walletapp.repository.MovementRepository;
import org.luisstubbia.walletapp.repository.SnapshotRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SnapshotServiceTest {

    @Mock
    private AccountBalanceRepository accountBalanceRepository;

    @Mock
    private MovementRepository movementRepository;

    @Mock
    private SnapshotRepository snapshotRepository;

    @InjectMocks
    private SnapshotService snapshotService;

    @BeforeEach
    void setUp() {
        snapshotService = new SnapshotService(accountBalanceRepository, movementRepository, snapshotRepository);
    }

    @Test
    void queueSnapshots_ShouldClearAndRepopulateQueue() {
        String balanceUid1 = "uid1";
        String balanceUid2 = "uid2";

        when(accountBalanceRepository.findAll()).thenReturn(Arrays.asList(
                createMockBalance(balanceUid1),
                createMockBalance(balanceUid2)
        ));

        snapshotService.getSnapshotQueue().add("oldItem");
        snapshotService.queueSnapshots();

        assertEquals(2, snapshotService.getSnapshotQueue().size());
        assertTrue(snapshotService.getSnapshotQueue().contains(balanceUid1));
        assertTrue(snapshotService.getSnapshotQueue().contains(balanceUid2));
        assertFalse(snapshotService.getSnapshotQueue().contains("oldItem"));
    }

    @Test
    void calculateSnapshots_WithEmptyQueue_ShouldDoNothing() {
        snapshotService.calculateSnapshots();

        assertTrue(snapshotService.getSnapshotQueue().isEmpty());
        verifyNoInteractions(movementRepository, snapshotRepository);
    }

    @Test
    void calculateSnapshots_WithItemInQueue_ShouldProcessSnapshot() {
        String balanceUid = "testUid";
        snapshotService.getSnapshotQueue().add(balanceUid);

        BigDecimal amount = BigDecimal.valueOf(100.0);
        LocalDate previousDate = LocalDate.now().minusDays(1);

        when(movementRepository.getAccumulateMovementByDateAndBalance(balanceUid, previousDate))
                .thenReturn(amount);
        snapshotService.calculateSnapshots();

        assertTrue(snapshotService.getSnapshotQueue().isEmpty());
        verify(movementRepository).getAccumulateMovementByDateAndBalance(balanceUid, previousDate);
        verify(snapshotRepository).save(any(MovementSnapshot.class));
    }

    @Test
    void processSnapshot_WithValidAmount_ShouldSaveSnapshot() {
        String balanceUid = "testUid";
        BigDecimal amount = BigDecimal.valueOf(150.0);
        LocalDate previousDate = LocalDate.now().minusDays(1);

        when(movementRepository.getAccumulateMovementByDateAndBalance(balanceUid, previousDate))
                .thenReturn(amount);

        snapshotService.processSnapshot(balanceUid);

        verify(snapshotRepository).save(argThat(ms ->
                ms.getBalanceUid().equals(balanceUid) &&
                        ms.getAmount().equals(amount) &&
                        ms.getCreatedDate().equals(previousDate) &&
                        ms.getUid().equals(balanceUid + previousDate)
        ));
    }

    @Test
    void processSnapshot_WithNullAmount_ShouldNotSaveSnapshot() {
        String balanceUid = "testUid";
        LocalDate previousDate = LocalDate.now().minusDays(1);

        when(movementRepository.getAccumulateMovementByDateAndBalance(balanceUid, previousDate))
                .thenReturn(null);
        snapshotService.processSnapshot(balanceUid);
        verifyNoInteractions(snapshotRepository);
    }

    @Test
    void getSnapshots_ShouldReturnRepositoryResult() {
        List<String> balanceUids = Arrays.asList("uid1", "uid2");
        LocalDate date = LocalDate.now();
        List<MovementSnapshot> expectedSnapshots = Arrays.asList(
                (MovementSnapshot.builder().uid("uid1").amount(BigDecimal.valueOf(100.0)).createdDate(date)).build(),
                (MovementSnapshot.builder().uid("uid2").amount(BigDecimal.valueOf(200.0)).createdDate(date)).build());

        when(snapshotRepository.findAllByBalanceUid(balanceUids, date))
                .thenReturn(expectedSnapshots);

        List<MovementSnapshot> result = snapshotService.getSnapshots(balanceUids, date);

        assertEquals(expectedSnapshots, result);
        verify(snapshotRepository).findAllByBalanceUid(balanceUids, date);
    }

    // Helper methods
    private AccountBalance createMockBalance(String uid) {
        return AccountBalance.builder()
                .uid(uid)
                .accountId(1L)
                .currency("ARS")
                .partition(BalancePartitionTypeEnum.AVAILABLE)
                .id(1L)
                .build();
    }
}