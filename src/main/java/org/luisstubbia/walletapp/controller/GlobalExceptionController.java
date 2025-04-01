package org.luisstubbia.walletapp.controller;

import org.luisstubbia.walletapp.exception.ConflictEntityException;
import org.luisstubbia.walletapp.exception.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.auth.login.AccountNotFoundException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handle(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConflictEntityException.class)
    public ResponseEntity<ErrorDetails> handle(Exception e) {
        var errorDetails = getDetails(e);
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorDetails> handle(AccountNotFoundException e) {
        var errorDetails = getDetails(e);
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    private ErrorDetails getDetails(Exception e) {
        return new ErrorDetails(
                LocalDateTime.now(),
                e.getCause().toString(),
                e.getMessage()
        );
    }
}