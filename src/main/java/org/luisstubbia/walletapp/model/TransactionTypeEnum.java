package org.luisstubbia.walletapp.model;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum TransactionTypeEnum {
    WITHDRAW("WITHDRAW"),
    TRANSFER("TRANSFER"),
    DEPOSIT("DEPOSIT");

    private final String code;

    TransactionTypeEnum(String code) {
        this.code = code;
    }

    public static Optional<TransactionTypeEnum> fromCode(String code) {
        for (TransactionTypeEnum status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return Optional.of(status);
            }
        }
        return Optional.empty();
    }
}
