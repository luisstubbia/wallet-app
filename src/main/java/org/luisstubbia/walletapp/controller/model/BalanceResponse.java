package org.luisstubbia.walletapp.controller.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Data
public class BalanceResponse {
    private String balanceUid;
    private BigDecimal amount;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String type;
}
