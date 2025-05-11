package com.familyHub.authorizationManager.controllers;

import com.familyHub.authorizationManager.dto.*;
import com.familyHub.authorizationManager.exceptions.AuthenticationException;
import com.familyHub.authorizationManager.models.User;
import com.familyHub.authorizationManager.security.JwtTokenProvider;
import com.familyHub.authorizationManager.security.UserPrincipal;
import com.familyHub.authorizationManager.services.UserService;
import com.familyHub.authorizationManager.services.impl.OtpService;
import com.familyHub.authorizationManager.services.IUserRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    @Autowired
    private JwtTokenProvider tokenProvider;
    private final OtpService otpService;
    private final IUserRegistrationService userRegistrationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );

            if (authentication.isAuthenticated()) {
                String token = tokenProvider.generateToken(authentication);
                UserDTO userDTO = userService.getUserByEmail(loginRequest.getEmail());
                return ResponseEntity.ok(new AuthResponse(token, userDTO));
            }

            return ResponseEntity.badRequest().body(new AuthResponse("Authentication failed", false));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse("Authentication failed: " + e.getMessage(), false));
        }
    }

    @PostMapping("/otp/generate")
    public ResponseEntity<?> generateOtp(@RequestBody OtpRequest request) {
        try {
            if (request.getType() == null || (!request.getType().equalsIgnoreCase("EMAIL") && !request.getType().equalsIgnoreCase("MOBILE"))) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse("Type must be either EMAIL or MOBILE", false));
            }

            String identifier = "EMAIL".equalsIgnoreCase(request.getType()) ? 
                request.getEmail() : request.getMobileNumber();
            
            if (identifier == null || identifier.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse("Please provide either email or mobile number", false));
            }

            otpService.generateOtp(identifier, request.getType());
            String message = "EMAIL".equalsIgnoreCase(request.getType()) ?
                "OTP sent to email successfully" : "OTP sent to mobile number successfully";
            return ResponseEntity.ok(new AuthResponse(message, true));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new AuthResponse("Failed to generate OTP: " + e.getMessage(), false));
        }
    }

    @PostMapping("/otp/validate")
    public ResponseEntity<?> validateOtp(@RequestBody OtpValidationRequest request) {
        try {
            if (request.getType() == null || (!request.getType().equalsIgnoreCase("EMAIL") && !request.getType().equalsIgnoreCase("MOBILE"))) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse("Type must be either EMAIL or MOBILE", false));
            }

            String identifier = "EMAIL".equalsIgnoreCase(request.getType()) ? 
                request.getEmail() : request.getMobileNumber();
            
            if (identifier == null || identifier.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new AuthResponse("Please provide either email or mobile number", false));
            }

            boolean isValid = otpService.validateOtp(identifier, request.getOtp(), request.getType());
            if (isValid) {
                User user = "EMAIL".equalsIgnoreCase(request.getType()) ?
                    userService.findUserByEmail(identifier) :
                    userService.findUserByMobileNumber(identifier);
                
                // Create an Authentication object for the user
                UserPrincipal userPrincipal = UserPrincipal.create(user);
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userPrincipal, null, userPrincipal.getAuthorities());
                
                String token = tokenProvider.generateToken(authentication);
                UserDTO userDTO = userService.getUserByEmail(user.getEmail());
                return ResponseEntity.ok(new AuthResponse(token, userDTO));
            }
            
            return ResponseEntity.badRequest().body(new AuthResponse("Invalid OTP", false));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new AuthResponse("Failed to validate OTP: " + e.getMessage(), false));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!tokenProvider.validateToken(token)) {
            throw new AuthenticationException("Invalid or expired token");
        }

        String userId = tokenProvider.getUserIdFromToken(token);
        UserDTO userDTO = userService.getUserById(userId);
        User user = userService.findUserById(userId);
        
        // Create an Authentication object for the user
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userPrincipal, null, userPrincipal.getAuthorities());
        
        String newToken = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new AuthResponse(newToken, userDTO));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        // In a stateless JWT setup, client-side logout is sufficient
        // Server-side blacklisting could be implemented here if needed
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        try {
            String token = userRegistrationService.processUserRegistration(userRegisterDTO, null);
            return ResponseEntity.ok(new AuthResponse(token, true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse("Registration failed: " + e.getMessage(), false));
        }
    }
} 