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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    @Autowired
    private JwtTokenProvider tokenProvider;
    private final OtpService otpService;
    private final IUserRegistrationService userRegistrationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest,
            @RequestHeader(REQUEST_ID_HEADER) String requestId) {
        logger.info("[{}] Login attempt for user: {}", requestId, loginRequest.getEmail());
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
                logger.info("[{}] Login successful for user: {}", requestId, loginRequest.getEmail());
                return ResponseEntity.ok(new AuthResponse(token, userDTO));
            }

            logger.warn("[{}] Login failed for user: {}", requestId, loginRequest.getEmail());
            return ResponseEntity.badRequest().body(new AuthResponse("Authentication failed", false));
        } catch (Exception e) {
            logger.error("[{}] Login error for user {}: {}", requestId, loginRequest.getEmail(), e.getMessage());
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
    public ResponseEntity<?> validateOtp(
            @RequestBody OtpValidationRequest request,
            @RequestHeader(REQUEST_ID_HEADER) String requestId) {
        logger.info("[{}] OTP validation attempt for type: {}", requestId, request.getType());
        try {
            if (request.getType() == null || (!request.getType().equalsIgnoreCase("EMAIL") && !request.getType().equalsIgnoreCase("MOBILE"))) {
                logger.warn("[{}] Invalid OTP validation type: {}", requestId, request.getType());
                return ResponseEntity.badRequest()
                    .body(new AuthResponse("Type must be either EMAIL or MOBILE", false));
            }

            String identifier = "EMAIL".equalsIgnoreCase(request.getType()) ? 
                request.getEmail() : request.getMobileNumber();
            
            if (identifier == null || identifier.trim().isEmpty()) {
                logger.warn("[{}] Missing identifier for OTP validation", requestId);
                return ResponseEntity.badRequest()
                    .body(new AuthResponse("Please provide either email or mobile number", false));
            }

            boolean isValid = otpService.validateOtp(identifier, request.getOtp(), request.getType());
            if (isValid) {
                User user = "EMAIL".equalsIgnoreCase(request.getType()) ?
                    userService.findUserByEmail(identifier) :
                    userService.findUserByMobileNumber(identifier);
                
                UserPrincipal userPrincipal = UserPrincipal.create(user);
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userPrincipal, null, userPrincipal.getAuthorities());
                
                String token = tokenProvider.generateToken(authentication);
                UserDTO userDTO = userService.getUserByEmail(user.getEmail());
                logger.info("[{}] OTP validation successful for: {}", requestId, identifier);
                return ResponseEntity.ok(new AuthResponse(token, userDTO));
            }
            
            logger.warn("[{}] Invalid OTP for: {}", requestId, identifier);
            return ResponseEntity.badRequest().body(new AuthResponse("Invalid OTP", false));
        } catch (Exception e) {
            logger.error("[{}] OTP validation error: {}", requestId, e.getMessage());
            return ResponseEntity.badRequest()
                .body(new AuthResponse("Failed to validate OTP: " + e.getMessage(), false));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestHeader("Authorization") String token,
            @RequestHeader(REQUEST_ID_HEADER) String requestId) {
        logger.info("[{}] Token refresh attempt", requestId);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!tokenProvider.validateToken(token)) {
            logger.warn("[{}] Invalid or expired token for refresh", requestId);
            throw new AuthenticationException("Invalid or expired token");
        }

        String userId = tokenProvider.getUserIdFromToken(token);
        UserDTO userDTO = userService.getUserById(userId);
        User user = userService.findUserById(userId);
        
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userPrincipal, null, userPrincipal.getAuthorities());
        
        String newToken = tokenProvider.generateToken(authentication);
        logger.info("[{}] Token refreshed successfully for user: {}", requestId, userId);
        return ResponseEntity.ok(new AuthResponse(newToken, userDTO));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String token,
            @RequestHeader(REQUEST_ID_HEADER) String requestId) {
        logger.info("[{}] Logout request received", requestId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody UserRegisterDTO userRegisterDTO,
            @RequestHeader(REQUEST_ID_HEADER) String requestId) {
        logger.info("[{}] Registration attempt for user: {}", requestId, userRegisterDTO.getEmail());
        try {
            String token = userRegistrationService.processUserRegistration(userRegisterDTO, null);
            logger.info("[{}] Registration successful for user: {}", requestId, userRegisterDTO.getEmail());
            return ResponseEntity.ok(new AuthResponse(token, true));
        } catch (Exception e) {
            logger.error("[{}] Registration failed for user {}: {}", requestId, userRegisterDTO.getEmail(), e.getMessage());
            return ResponseEntity.badRequest().body(new AuthResponse("Registration failed: " + e.getMessage(), false));
        }
    }
} 