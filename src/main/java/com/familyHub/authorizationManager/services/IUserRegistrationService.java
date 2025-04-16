package com.familyHub.authorizationManager.services;

import com.familyHub.authorizationManager.dto.SetUpPasswordRequest;
import com.familyHub.authorizationManager.dto.UserRegisterDTO;
import org.springframework.security.core.Authentication;

/**
 * Service interface for user registration operations.
 * Handles user registration and password setup processes.
 */
public interface IUserRegistrationService {
    
    /**
     * Processes user registration with email notification.
     *
     * @param userRegisterDTO The user registration data
     * @param authentication The current authentication context
     * @return Registration token for password setup
     */
    String processUserRegistration(UserRegisterDTO userRegisterDTO, Authentication authentication);

    /**
     * Sets up a new password for a family member.
     * Validates and updates the password in the database.
     * The userId is extracted from the JWT token in the current security context.
     *
     * @param request The password setup request
     * @throws PasswordSetupException if password setup fails
     */
    void setUpFamilyMemberPassword(SetUpPasswordRequest request);
} 