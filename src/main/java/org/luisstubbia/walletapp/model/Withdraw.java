package org.luisstubbia.walletapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Builder
@Table(name = "withdraw")
public class Withdraw implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id")
    private Long accountId;
    private BigDecimal amount;
    private BigDecimal fee;
    private String currency;
    private String comment;
    private String uid;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "bank_account_number")
    private String bankAccountNbr;
}
