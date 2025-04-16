package com.familyHub.authorizationManager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for setting up a new password for a family member.
 * Contains validation for password requirements and confirmation.
 */
@Data
public class SetUpPasswordRequest {
    
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String newPassword;

    @NotBlank(message = "Confirm password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String confirmPassword;
} 