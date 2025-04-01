package org.luisstubbia.walletapp.controller.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AccountBalanceRequest {
    private String currency;
    private String partition;
}
