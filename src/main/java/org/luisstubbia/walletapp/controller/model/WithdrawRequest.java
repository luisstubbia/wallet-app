package org.luisstubbia.walletapp.controller.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
public class WithdrawRequest implements Hashable {
    private String bankAccount;
    private BigDecimal amount;
    private BigDecimal fee;
    private String currency;
    private String comment;
    private LocalDateTime date;

    @Override
    public String toString() {
        return "bankAccount:" + bankAccount +
                "amount:" + amount +
                "fee:" + fee +
                "currency:" + currency +
                "comment:" + comment +
                "date:" + date;
    }
}
