package com.familyHub.authorizationManager.controllers;

import com.familyHub.authorizationManager.dto.*;
import com.familyHub.authorizationManager.exceptions.AuthenticationException;
import com.familyHub.authorizationManager.models.User;
import com.familyHub.authorizationManager.security.JwtTokenProvider;
import com.familyHub.authorizationManager.services.UserService;
import com.familyHub.authorizationManager.services.OtpService;
import lombok.RequiredArgsConstructor;
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
    private final JwtTokenProvider tokenProvider;
    private final OtpService otpService;

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
                User user = userService.findUserByEmail(loginRequest.getEmail());
                String token = tokenProvider.generateToken(user);
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
                
                String token = tokenProvider.generateToken(user);
                UserDTO userDTO = userService.getUserByEmail(user.getEmail());
                return ResponseEntity.ok(new AuthResponse(token, userDTO));
            }
            return ResponseEntity.badRequest().body(new AuthResponse("Invalid OTP", false));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new AuthResponse("OTP validation failed: " + e.getMessage(), false));
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
        
        String newToken = tokenProvider.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(newToken, userDTO));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        // In a stateless JWT setup, client-side logout is sufficient
        // Server-side blacklisting could be implemented here if needed
        return ResponseEntity.ok().build();
    }
} 