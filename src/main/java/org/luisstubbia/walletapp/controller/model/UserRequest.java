package org.luisstubbia.walletapp.controller.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class UserRequest {
    private String username;
    private String password;
    private String name;
    private String lastName;
    private String email;
}
