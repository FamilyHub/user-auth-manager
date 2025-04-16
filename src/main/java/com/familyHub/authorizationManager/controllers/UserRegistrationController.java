package com.familyHub.authorizationManager.controllers;

import com.familyHub.authorizationManager.dto.RegistrationResponse;
import com.familyHub.authorizationManager.dto.SetUpPasswordRequest;
import com.familyHub.authorizationManager.dto.SetUpPasswordResponse;
import com.familyHub.authorizationManager.dto.UserRegisterDTO;
import com.familyHub.authorizationManager.exceptions.PasswordSetupException;
import com.familyHub.authorizationManager.services.IUserRegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/family-member")
public class UserRegistrationController {

    @Autowired
    private IUserRegistrationService userRegistrationService;

    @PostMapping("/add-new-user")
    public ResponseEntity<RegistrationResponse> addNewUser(
            @RequestBody UserRegisterDTO userRegisterDTO, 
            Authentication authentication) {
        try {
            String registrationToken = userRegistrationService.processUserRegistration(userRegisterDTO, authentication);
            return ResponseEntity.ok(new RegistrationResponse(
                "Registration email sent successfully",
                registrationToken,
                true
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new RegistrationResponse(
                "Error processing registration: " + e.getMessage(),
                null,
                false
            ));
        }
    }

    /**
     * Endpoint for setting up a family member's password.
     * Validates the password and updates it in the database.
     * The userId is extracted from the JWT token in the Authorization header.
     *
     * @param request The password setup request containing new and confirm passwords
     * @return Response indicating success or failure of the password setup
     */
    @PostMapping("/set-up-password")
    public ResponseEntity<SetUpPasswordResponse> setUpFamilyMemberPassword(
            @Valid @RequestBody SetUpPasswordRequest request) {
        try {
            userRegistrationService.setUpFamilyMemberPassword(request);
            return ResponseEntity.ok(new SetUpPasswordResponse(
                "Password set up successfully",
                true,
                200
            ));
        } catch (PasswordSetupException e) {
            return ResponseEntity.badRequest().body(new SetUpPasswordResponse(
                e.getMessage(),
                false,
                400
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new SetUpPasswordResponse(
                "An error occurred while setting up the password",
                false,
                500
            ));
        }
    }
} 