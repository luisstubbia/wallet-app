package org.luisstubbia.walletapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.luisstubbia.walletapp.controller.model.BalanceResponse;
import org.luisstubbia.walletapp.model.User;
import org.luisstubbia.walletapp.service.AccountService;
import org.luisstubbia.walletapp.service.ProjectionService;
import org.luisstubbia.walletapp.service.SnapshotService;
import org.luisstubbia.walletapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.AccountNotFoundException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "Balance", description = "Balance API allows to retrieve user balance's amount")
@RestController
public class AccountBalanceController {
    private final ProjectionService projectionService;
    private final SnapshotService snapshotService;
    private final UserService userService;
    private final AccountService accountService;

    public AccountBalanceController(ProjectionService projectionService, SnapshotService snapshotService, UserService userService, AccountService accountService) {
        this.projectionService = projectionService;
        this.snapshotService = snapshotService;
        this.userService = userService;
        this.accountService = accountService;
    }

    @Operation(summary = "Retrieve balances amount o near real-time")
    @ApiResponse(responseCode = "200", description = "Successful balance response")
    @GetMapping("/accounts/balances")
    public ResponseEntity<List<BalanceResponse>> retrieveBalance(@RequestParam(name = "date", required = false) LocalDate date, Principal principal) throws AccountNotFoundException {
        var user = getUser(principal);
        var acc = accountService.getAccountByUser(user.getId());
        List<String> blcUids = new ArrayList<>();
        acc.getBalances().forEach(balance -> blcUids.add(balance.getUid()));
        List<BalanceResponse> blcRspList = new ArrayList<>();
        if (date == null) {
            // Get real-time balance
            var prjs = projectionService.getProjections(blcUids);
            prjs.forEach(prj -> {
                blcRspList.add(BalanceResponse.builder()
                        .balanceUid(prj.getBalanceUid())
                        .amount(prj.getAmount())
                        .createdDate(prj.getCreatedDate())
                        .updatedDate(prj.getUpdatedDate())
                        .type("PROJECTION")
                        .build());
            });
            return ResponseEntity.ok(blcRspList);
        }
        var snps = snapshotService.getSnapshots(blcUids, date);
        snps.forEach(snp -> {
            var ld = snp.getCreatedDate();
            var ldt = LocalDateTime.of(ld.getYear(), ld.getMonth(), ld.getDayOfMonth(), 0,0,0);
            blcRspList.add(BalanceResponse.builder()
                    .balanceUid(snp.getBalanceUid())
                    .amount(snp.getAmount())
                    .createdDate(ldt)
                    .updatedDate(ldt)
                    .type("SNAPSHOT")
                    .build());
        });
        return ResponseEntity.ok(blcRspList);
    }

    private User getUser(Principal principal) {
        return userService.retrieveUserByUsername(principal.getName());
    }
}
