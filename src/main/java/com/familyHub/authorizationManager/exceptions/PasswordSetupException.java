package com.familyHub.authorizationManager.exceptions;

/**
 * Custom exception for password setup related errors.
 * Used to handle specific password setup failure scenarios.
 */
public class PasswordSetupException extends RuntimeException {
    
    public PasswordSetupException(String message) {
        super(message);
    }

    public PasswordSetupException(String message, Throwable cause) {
        super(message, cause);
    }
} 