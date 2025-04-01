package org.luisstubbia.walletapp.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "balance_projection")
public class AccountBalanceProjection implements Serializable {
    @Id
    @Column(name = "balance_uid")
    private String balanceUid;
    private BigDecimal amount;

    @Column(name = "last_movement_id")
    private Long lastMovementId;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    private Long version;
}
