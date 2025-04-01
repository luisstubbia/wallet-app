package org.luisstubbia.walletapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.luisstubbia.walletapp.controller.model.DepositRequest;
import org.luisstubbia.walletapp.model.Deposit;
import org.luisstubbia.walletapp.model.User;
import org.luisstubbia.walletapp.service.AccountService;
import org.luisstubbia.walletapp.service.DepositService;
import org.luisstubbia.walletapp.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@Tag(name = "Deposit", description = "Deposit API allows to apply funding for logged user's account")
@RestController
public class DepositController {

    private final DepositService depositService;
    private final AccountService accountService;
    private final UserService userService;

    public DepositController(DepositService depositService, AccountService accountService, UserService userService, UserService userService1) {
        this.depositService = depositService;
        this.accountService = accountService;
        this.userService = userService1;
    }


    @Operation(summary = "create a new deposit")
    @ApiResponse(responseCode = "200", description = "Successful deposit created")
    @PostMapping("/deposits")
    public Deposit createDeposit(@RequestBody DepositRequest req, Principal principal) throws Exception{
        var user = getUser(principal);
        var acc = accountService.getAccountByUser(user.getId());
        var dp = Deposit.builder()
                .accountId(acc.getId())
                .bankAccountNbr(req.getBankAccount())
                .amount(req.getAmount())
                .comment(req.getComment())
                .currency(req.getCurrency())
                .createdDate(req.getDate())
                .uid(req.getHash())
                .build();
        return depositService.createDeposit(dp);
    }

    @Operation(summary = "Retrieve all deposits")
    @ApiResponse(responseCode = "200", description = "Successful deposit list")
    @GetMapping("/deposits")
    public List<Deposit> getDeposits(Principal principal) throws Exception{
        var user = getUser(principal);
        var acc = accountService.getAccountByUser(user.getId());
        return depositService.getDeposits(acc.getId());
    }

    public User getUser(Principal principal) {
        return userService.retrieveUserByUsername(principal.getName());
    }
}
