package org.luisstubbia.walletapp.model;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum AccountStatusEnum {
    PENDING("PENDING"),
    ACTIVE("ACTIVE"),
    BLOCKED("BLOCKED"),
    CLOSED("CLOSED");

    private final String code;

    AccountStatusEnum(String code) {
        this.code = code;
    }

    public static Optional<AccountStatusEnum> fromCode(String code) {
        if (code != null) {
            for (AccountStatusEnum status : values()) {
                if (status.code.equalsIgnoreCase(code)) {
                    return Optional.of(status);
                }
            }
        }
        return Optional.empty();
    }
}
