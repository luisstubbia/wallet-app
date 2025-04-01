package org.luisstubbia.walletapp.controller.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

public interface Hashable {
    @JsonIgnore
    default String getHash() {
        var uuid = UUID.nameUUIDFromBytes(this.toString().getBytes());
        return uuid.toString();
    }
}
