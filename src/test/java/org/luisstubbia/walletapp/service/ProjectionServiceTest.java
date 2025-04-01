package org.luisstubbia.walletapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.luisstubbia.walletapp.model.AccountBalance;
import org.luisstubbia.walletapp.model.AccountBalanceProjection;
import org.luisstubbia.walletapp.model.BalancePartitionTypeEnum;
import org.luisstubbia.walletapp.model.Movement;
import org.luisstubbia.walletapp.repository.AccountBalanceRepository;
import org.luisstubbia.walletapp.repository.MovementRepository;
import org.luisstubbia.walletapp.repository.ProjectionRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectionServiceTest {

    @Mock
    private AccountBalanceRepository accountBalanceRepository;

    @Mock
    private MovementRepository movementRepository;

    @Mock
    private ProjectionRepository projectionRepository;

    @Mock
    private Queue<Object> projectionQueue;

    @InjectMocks
    private ProjectionService projectionService;

    private final String BALANCE_UID = "balance123";
    private final String BALANCE_UID2 = "balance456";
    private final Long MOVEMENT_ID = 1L;
    private final Long VERSION = 0L;

    @BeforeEach
    void setUp() {
        projectionService = new ProjectionService(
                accountBalanceRepository,
                movementRepository,
                projectionRepository
        );
    }

    @Test
    void queueProjections_ShouldAddAllBalanceUidsToQueue() {
        when(accountBalanceRepository.findAll()).thenReturn(Arrays.asList(
                createMockBalance(BALANCE_UID),
                createMockBalance(BALANCE_UID2)
        ));

        // Act
        projectionService.queueProjections();
        assertEquals(2, projectionService.getQueueSize());
    }

    @Test
    void forceSync_ShouldAddAllProvidedUidsToQueue() {
        List<String> uids = Arrays.asList(BALANCE_UID, BALANCE_UID2);
        projectionService.forceSync(uids);
        assertEquals(2, projectionService.getQueueSize());
    }

    @Test
    void calculateProjection_WithEmptyQueue_ShouldDoNothing() {
        projectionService.calculateProjection();
        verifyNoInteractions(projectionRepository);
        verifyNoInteractions(movementRepository);
    }

    @Test
    void updateProjection_WhenNoExistingProjection_ShouldCreateNewProjection() {
        when(projectionRepository.findByBalanceUid(BALANCE_UID)).thenReturn(null);

        Movement movement = createMovement(BigDecimal.TEN, MOVEMENT_ID);
        when(movementRepository.findAllByBalanceUid(BALANCE_UID))
                .thenReturn(Collections.singletonList(movement));

        projectionService.updateProjection(BALANCE_UID);

        verify(projectionRepository).optimisticLockingPersist(
                eq(BALANCE_UID),
                eq(BigDecimal.TEN),
                eq(MOVEMENT_ID),
                any(LocalDateTime.class),
                eq(0L)
        );
    }

    @Test
    void updateProjection_WhenNoMovements_ShouldDoNothing() {
        when(projectionRepository.findByBalanceUid(BALANCE_UID)).thenReturn(null);
        when(movementRepository.findAllByBalanceUid(BALANCE_UID))
                .thenReturn(Collections.emptyList());

        projectionService.updateProjection(BALANCE_UID);

        verify(projectionRepository, never()).optimisticLockingPersist(any(), any(), any(), any(), any());
    }

    @Test
    void updateProjection_WithExistingProjection_ShouldUpdateProjection() {
        AccountBalanceProjection existingProjection = new AccountBalanceProjection();
        existingProjection.setVersion(VERSION);
        existingProjection.setLastMovementId(MOVEMENT_ID);

        when(projectionRepository.findByBalanceUid(BALANCE_UID)).thenReturn(existingProjection);

        Movement newMovement = createMovement(BigDecimal.valueOf(5), MOVEMENT_ID + 1);
        when(movementRepository.findAllGreaterThanLastId(BALANCE_UID, existingProjection.getLastMovementId()))
                .thenReturn(Collections.singletonList(newMovement));

        projectionService.updateProjection(BALANCE_UID);
        verify(projectionRepository).optimisticLockingUpdate(
                eq(BALANCE_UID),
                eq(BigDecimal.valueOf(5)),
                eq(MOVEMENT_ID + 1),
                any(LocalDateTime.class),
                eq(VERSION)
        );
    }

    @Test
    void getProjections_ShouldReturnProjectionsForRequestedUids() {
        List<String> requestedUids = Arrays.asList(BALANCE_UID, BALANCE_UID2);
        AccountBalanceProjection projection1 = new AccountBalanceProjection();
        AccountBalanceProjection projection2 = new AccountBalanceProjection();

        when(projectionRepository.findAllByBalanceUid(requestedUids))
                .thenReturn(Arrays.asList(projection1, projection2));

        List<AccountBalanceProjection> result = projectionService.getProjections(requestedUids);

        assertEquals(2, result.size());
        verify(projectionRepository).findAllByBalanceUid(requestedUids);
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

    private Movement createMovement(BigDecimal amount, Long id) {
        Movement movement = new Movement();
        movement.setAmount(amount);
        movement.setId(id);
        return movement;
    }
}