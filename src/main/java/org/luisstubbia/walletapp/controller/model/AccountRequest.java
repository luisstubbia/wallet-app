package org.luisstubbia.walletapp.controller.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Data
public class AccountRequest {
    private String name;
    private String status;
}
