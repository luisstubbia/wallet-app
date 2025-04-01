package org.luisstubbia.walletapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "movements")
public class Movement implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uid", unique = true)
    private String uid;

    @Column(name = "balance_uid")
    private String balanceUid;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private MovementTypeEnum type;

    @Column(name = "amount")
    private BigDecimal amount;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "reference_type")
    @Enumerated(EnumType.STRING)
    private TransactionTypeEnum referenceType;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "reference_date")
    private LocalDateTime referenceDate;
}
