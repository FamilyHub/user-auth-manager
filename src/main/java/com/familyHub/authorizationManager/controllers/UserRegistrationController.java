package com.familyHub.authorizationManager.controllers;

import com.familyHub.authorizationManager.dto.UserRegisterDTO;
import com.familyHub.authorizationManager.services.IUserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/family-member")
public class UserRegistrationController {

    @Autowired
    private IUserRegistrationService userRegistrationService;

    @PostMapping("/add-new-user")
    public ResponseEntity<?> addNewUser(@RequestBody UserRegisterDTO userRegisterDTO, Authentication authentication) {
        try {
            String registrationToken = userRegistrationService.processUserRegistration(userRegisterDTO, authentication);
            return ResponseEntity.ok().body("Registration email sent successfully. Token: " + registrationToken);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing registration: " + e.getMessage());
        }
    }
} 