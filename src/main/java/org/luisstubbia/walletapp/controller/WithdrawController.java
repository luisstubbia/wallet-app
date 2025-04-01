package org.luisstubbia.walletapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.luisstubbia.walletapp.controller.model.WithdrawRequest;
import org.luisstubbia.walletapp.model.User;
import org.luisstubbia.walletapp.model.Withdraw;
import org.luisstubbia.walletapp.service.AccountService;
import org.luisstubbia.walletapp.service.UserService;
import org.luisstubbia.walletapp.service.WithdrawService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@Tag(name = "Withdraw", description = "Withdraw API applied into user's account logged")
@RestController
public class WithdrawController {

    private final WithdrawService withdrawService;
    private final AccountService accountService;
    private final UserService userService;

    public WithdrawController(WithdrawService withdrawService, AccountService accountService, UserService userService) {
        this.withdrawService = withdrawService;
        this.accountService = accountService;
        this.userService = userService;
    }


    @Operation(summary = "create a new withdraw")
    @ApiResponse(responseCode = "200", description = "Successful withdraw created")
    @PostMapping("/withdrawals")
    public Withdraw createWithdraw(@RequestBody WithdrawRequest req, Principal principal) throws Exception{
        var user = getUser(principal);
        var acc = accountService.getAccountByUser(user.getId());
        var wdw = Withdraw.builder()
                .accountId(acc.getId())
                .bankAccountNbr(req.getBankAccount())
                .amount(req.getAmount())
                .comment(req.getComment())
                .currency(req.getCurrency())
                .fee(req.getFee())
                .createdDate(req.getDate())
                .uid(req.getHash())
                .build();
        return withdrawService.createWithdraw(wdw);
    }

    @Operation(summary = "Retrieve all withdrawals")
    @ApiResponse(responseCode = "200", description = "Successful withdraw list")
    @GetMapping("/withdrawals")
    public List<Withdraw> getWithdrawals(Principal principal) throws Exception{
        var user = getUser(principal);
        var acc = accountService.getAccountByUser(user.getId());
        return withdrawService.getWithdrawals(acc.getId());
    }

    private User getUser(Principal principal) {
        return userService.retrieveUserByUsername(principal.getName());
    }
}
