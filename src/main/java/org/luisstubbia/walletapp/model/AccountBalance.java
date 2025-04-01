package org.luisstubbia.walletapp.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "account_balances")
public class AccountBalance implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "uid", unique = true)
    private String uid;

    @Column(name = "currency")
    private String currency;

    @Column(name = "balance_partition")
    @Enumerated(EnumType.STRING)
    private BalancePartitionTypeEnum partition;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    public String formatUid() {
        return String.format("%s-%s-%d", this.getPartition(), this.getCurrency(), this.getAccountId());
    }
}
