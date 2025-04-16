package com.familyHub.authorizationManager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for the response after setting up a new password.
 * Contains status information and message for the client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetUpPasswordResponse {
    private String message;
    private boolean success;
    private int statusCode;
} 