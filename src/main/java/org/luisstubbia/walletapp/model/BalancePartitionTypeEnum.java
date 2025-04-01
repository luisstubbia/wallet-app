package org.luisstubbia.walletapp.model;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum BalancePartitionTypeEnum {
    AVAILABLE("AVAILABLE"),
    BLOCKED("BLOCKED"),
    IN_TRANSIT("IN_TRANSIT"),
    RESERVE("RESERVE");

    private final String code;

    BalancePartitionTypeEnum(String code) {
        this.code = code;
    }

    public static Optional<BalancePartitionTypeEnum> fromCode(String code) {
        for (BalancePartitionTypeEnum status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return Optional.of(status);
            }
        }
        return Optional.empty();
    }
}
