package org.luisstubbia.walletapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.luisstubbia.walletapp.controller.model.TransferRequest;
import org.luisstubbia.walletapp.model.Transfer;
import org.luisstubbia.walletapp.model.User;
import org.luisstubbia.walletapp.service.AccountService;
import org.luisstubbia.walletapp.service.TransferService;
import org.luisstubbia.walletapp.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Transfer", description = "Transfer API in order to create and process transfer between account")
@RestController
public class TransferController {

    private final TransferService transferService;
    private final AccountService accountService;
    private final UserService userService;

    public TransferController(TransferService transferService, AccountService accountService, UserService userService) {
        this.transferService = transferService;
        this.accountService = accountService;
        this.userService = userService;
    }

    @Operation(summary = "create a new transfer")
    @ApiResponse(responseCode = "200", description = "Successful transfer created")
    @PostMapping("/transfers")
    public Transfer createTransfer(@RequestBody TransferRequest req, Principal principal) throws Exception{
        var user = getUser(principal);
        var acc = accountService.getAccountByUser(user.getId());
        var trf = Transfer.builder()
                .fromAccountId(acc.getId())
                .toAccountId(req.getToAccountId())
                .amount(req.getAmount())
                .comment(req.getComment())
                .currency(req.getCurrency())
                .fee(req.getFee())
                .createdDate(req.getDate())
                .uid(req.getHash())
                .build();
        return transferService.createTransfer(trf);
    }

    @Operation(summary = "Retrieve all transfers")
    @ApiResponse(responseCode = "200", description = "Successful transfer list")
    @GetMapping("/transfers")
    public Map<String, List<Transfer>> getTransfer(Principal principal) throws Exception{
        var user = getUser(principal);
        var acc = accountService.getAccountByUser(user.getId());
        var trfList = transferService.getTransfers(acc.getId());
        Map<String, List<Transfer>> transferMap = new HashMap<>() {{
                put("IN", new ArrayList<>());
                put("OUT", new ArrayList<>());
        }};

        trfList.forEach(trf -> {
            if (trf.getFromAccountId().equals(acc.getId())){
                transferMap.get("OUT").add(trf);
            }
            if (trf.getToAccountId().equals(acc.getId())){
                transferMap.get("IN").add(trf);
            }
        });
        return transferMap;
    }

    private User getUser(Principal principal) {
        return userService.retrieveUserByUsername(principal.getName());
    }
}
