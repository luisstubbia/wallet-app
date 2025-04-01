package org.luisstubbia.walletapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "movement_snapshots")
public class MovementSnapshot implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uid", unique = true)
    private String uid;

    @Column(name = "balance_uid")
    private String balanceUid;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "created_date")
    private LocalDate createdDate;
}
