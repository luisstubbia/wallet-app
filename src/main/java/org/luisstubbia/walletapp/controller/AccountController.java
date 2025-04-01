package org.luisstubbia.walletapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.BadRequestException;
import org.luisstubbia.walletapp.controller.model.AccountBalanceRequest;
import org.luisstubbia.walletapp.controller.model.AccountRequest;
import org.luisstubbia.walletapp.exception.ConflictEntityException;
import org.luisstubbia.walletapp.model.*;
import org.luisstubbia.walletapp.service.AccountBalanceService;
import org.luisstubbia.walletapp.service.AccountService;
import org.luisstubbia.walletapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.AccountNotFoundException;
import java.security.Principal;

@Tag(name = "Account", description = "Account API allows to handle User's account and balances")
@RestController
public class AccountController {

    private final AccountBalanceService accountBalanceService;
    private final AccountService accountService;
    private final UserService userService;

    public AccountController(AccountBalanceService accountBalanceService, AccountService accountService, UserService userService, UserService userService1) {
        this.accountBalanceService = accountBalanceService;
        this.accountService = accountService;
        this.userService = userService1;
    }

    @Operation(summary = "Retrieve Account")
    @ApiResponse(responseCode = "200", description = "Successful account")
    @GetMapping("/accounts")
    public ResponseEntity<Account> retrieveAccount(Principal principal) throws Exception {
        var user = getUser(principal);
        return new ResponseEntity<>(accountService.getAccountByUser(user.getId()), HttpStatus.OK);
    }

    @Operation(summary = "Create new Account")
    @ApiResponse(responseCode = "200", description = "Successful new account")
    @PostMapping("/accounts")
    public ResponseEntity<Account> createAccount(@RequestBody AccountRequest request, Principal principal) throws ConflictEntityException {
        var user = getUser(principal);
        Account acc = Account.builder().name(request.getName()).userId(user.getId()).status(AccountStatusEnum.ACTIVE).build();
        return new ResponseEntity<>(accountService.createAccount(acc), HttpStatus.OK);
    }

    @Operation(summary = "Associate balances to current Account")
    @ApiResponse(responseCode = "200", description = "Successful new account")
    @PostMapping("/accounts/balances")
    public ResponseEntity<Account> createAccountBalance(@RequestBody AccountBalanceRequest request, Principal principal) throws BadRequestException, AccountNotFoundException {
        var user = getUser(principal);
        var partition = BalancePartitionTypeEnum.fromCode(request.getPartition());
        if (partition.isEmpty()) {
            throw new BadRequestException("Invalid partition");
        }
        var acc = accountService.getAccountByUser(user.getId());
        AccountBalance accBlc = AccountBalance.builder().currency(request.getCurrency()).partition(partition.get()).accountId(acc.getId()).build();
        accountBalanceService.createAccountBalance(accBlc);
        return new ResponseEntity<>(acc, HttpStatus.OK);
    }

    private User getUser(Principal principal) {
        return userService.retrieveUserByUsername(principal.getName());
    }
}
