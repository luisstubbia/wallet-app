package org.luisstubbia.walletapp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.luisstubbia.walletapp.controller.model.DepositRequest;
import org.luisstubbia.walletapp.model.Account;
import org.luisstubbia.walletapp.model.Deposit;
import org.luisstubbia.walletapp.model.User;
import org.luisstubbia.walletapp.service.AccountService;
import org.luisstubbia.walletapp.service.DepositService;
import org.luisstubbia.walletapp.service.UserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepositControllerTest {

    @Mock
    private DepositService depositService;

    @Mock
    private AccountService accountService;

    @Mock
    private UserService userService;

    @Mock
    private Principal principal;

    @InjectMocks
    private DepositController depositController;

    private User testUser;
    private Deposit testDeposit;
    private DepositRequest testRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testDeposit = Deposit.builder()
                .id(1L)
                .accountId(1L)
                .amount(BigDecimal.valueOf(100.0))
                .currency("USD")
                .build();

        testRequest = DepositRequest.builder()
                .amount(BigDecimal.valueOf(100.0))
                .currency("USD")
                .bankAccount("123456789")
                .date(LocalDateTime.now())
                .comment("Test deposit")
                .build();
    }

    @Test
    void createDeposit_ShouldCreateNewDeposit() throws Exception {
        when(principal.getName()).thenReturn("testuser");
        when(userService.retrieveUserByUsername("testuser")).thenReturn(testUser);
        when(accountService.getAccountByUser(1L)).thenReturn(Account.builder().name("ACC123").id(1L).build());
        when(depositService.createDeposit(any(Deposit.class))).thenReturn(testDeposit);

        Deposit result = depositController.createDeposit(testRequest, principal);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("100.0", result.getAmount().toString());

        verify(userService).retrieveUserByUsername("testuser");
        verify(accountService).getAccountByUser(1L);
        verify(depositService).createDeposit(any(Deposit.class));
    }

    @Test
    void createDeposit_ShouldMapRequestCorrectly() throws Exception {
        when(principal.getName()).thenReturn("testuser");
        when(userService.retrieveUserByUsername("testuser")).thenReturn(testUser);
        when(accountService.getAccountByUser(1L)).thenReturn(Account.builder().name("ACC123").id(1L).build());
        when(depositService.createDeposit(any(Deposit.class))).thenReturn(testDeposit);

        depositController.createDeposit(testRequest, principal);

        verify(depositService).createDeposit(argThat(deposit ->
                deposit.getAmount().equals(BigDecimal.valueOf(100.0)) &&
                        deposit.getCurrency().equals("USD") &&
                        deposit.getBankAccountNbr().equals("123456789") &&
                        deposit.getComment().equals("Test deposit") &&
                        deposit.getAccountId() == 1L
        ));
    }

    @Test
    void getDeposits_ShouldReturnDepositsForUserAccount() throws Exception {
        List<Deposit> expectedDeposits = Arrays.asList(
                testDeposit,
                Deposit.builder().id(2L).accountId(1L).amount(BigDecimal.valueOf(200.0)).currency("USD").build()
        );

        when(principal.getName()).thenReturn("testuser");
        when(userService.retrieveUserByUsername("testuser")).thenReturn(testUser);
        when(accountService.getAccountByUser(1L)).thenReturn(Account.builder().name("ACC123").id(1L).build());
        when(depositService.getDeposits(1L)).thenReturn(expectedDeposits);

        // Act
        List<Deposit> result = depositController.getDeposits(principal);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());

        verify(userService).retrieveUserByUsername("testuser");
        verify(accountService).getAccountByUser(1L);
        verify(depositService).getDeposits(1L);
    }

    @Test
    void getUser_ShouldReturnUserFromPrincipal() {
        when(principal.getName()).thenReturn("testuser");
        when(userService.retrieveUserByUsername("testuser")).thenReturn(testUser);

        User result = depositController.getUser(principal);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
    }
}