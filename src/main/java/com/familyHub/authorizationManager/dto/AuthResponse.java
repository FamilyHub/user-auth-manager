package com.familyHub.authorizationManager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private UserDTO user;
    private String message;
    private boolean success;

    public AuthResponse(String token, UserDTO user) {
        this.token = token;
        this.user = user;
        this.success = true;
    }

    public AuthResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
} 