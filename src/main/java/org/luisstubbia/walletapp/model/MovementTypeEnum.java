package org.luisstubbia.walletapp.model;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum MovementTypeEnum {
    CREDIT(1),
    DEBIT(0);

    private final int code;

    MovementTypeEnum(int code) {
        this.code = code;
    }

    public static Optional<MovementTypeEnum> fromCode(int code) {
        for (MovementTypeEnum status : values()) {
            if (status.code == code) {
                return Optional.of(status);
            }
        }
        return Optional.empty();
    }
}
