package org.luisstubbia.walletapp.exception;

import java.time.LocalDateTime;

public record ErrorDetails(LocalDateTime timestamp, String rootCause, String errorMessage) {
}
