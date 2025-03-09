package com.familyHub.authorizationManager.exceptions;

import org.springframework.http.HttpStatus;

public class NotAllowedException extends RuntimeException{
    private final HttpStatus status;
    private final String message;

    public NotAllowedException(String message, HttpStatus status) {
        super(message);
        this.message = message;
        this.status = status;
    }
}
