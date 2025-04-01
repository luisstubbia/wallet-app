package org.luisstubbia.walletapp.controller.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
@Builder
public class JwtResponse implements Serializable {
    private String jwtToken;
}
