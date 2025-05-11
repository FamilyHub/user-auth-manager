package com.familyHub.authorizationManager.controllers;

import com.familyHub.authorizationManager.models.User;
import com.familyHub.authorizationManager.services.TokenInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/token")
public class TokenInfoController {

    @Autowired
    private TokenInfoService tokenInfoService;

    // Get all token information
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getTokenInfo() {
        return ResponseEntity.ok(tokenInfoService.getTokenInfo());
    }

    // Get user ID
    @GetMapping("/user-id")
    public ResponseEntity<String> getUserId() {
        return ResponseEntity.ok(tokenInfoService.getUserId());
    }

    // Get email
    @GetMapping("/email")
    public ResponseEntity<?> getEmail() {
        try {
            String email = tokenInfoService.getEmail();
            return ResponseEntity.ok(Map.of("email", email));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get name
    @GetMapping("/name")
    public ResponseEntity<String> getName() {
        return ResponseEntity.ok(tokenInfoService.getName());
    }

    // Get family name
    @GetMapping("/family-name")
    public ResponseEntity<String> getFamilyName() {
        return ResponseEntity.ok(tokenInfoService.getFamilyName());
    }

    // Get user level
    @GetMapping("/user-level")
    public ResponseEntity<String> getUserLevel() {
        return ResponseEntity.ok(tokenInfoService.getUserLevel());
    }

    // Get roles
    @GetMapping("/roles")
    public ResponseEntity<?> getRoles() {
        return ResponseEntity.ok(tokenInfoService.getRoles());
    }

    // Get complete user info
    @GetMapping("/user")
    public ResponseEntity<User> getUser() {
        return ResponseEntity.ok(tokenInfoService.getUser());
    }

    // Get mobile number
    @GetMapping("/mobile-number")
    public ResponseEntity<String> getMobileNumber() {
        return ResponseEntity.ok(tokenInfoService.getMobileNumber());
    }

    // Get custom fields
    @GetMapping("/custom-fields")
    public ResponseEntity<?> getCustomFields() {
        return ResponseEntity.ok(tokenInfoService.getCustomFields());
    }

    // Get token expiration time
    @GetMapping("/expiration")
    public ResponseEntity<Long> getTokenExpiration() {
        return ResponseEntity.ok(tokenInfoService.getTokenExpiration());
    }

    // Get token issued at time
    @GetMapping("/issued-at")
    public ResponseEntity<Long> getTokenIssuedAt() {
        return ResponseEntity.ok(tokenInfoService.getTokenIssuedAt());
    }

    // Get token subject (user ID)
    @GetMapping("/subject")
    public ResponseEntity<String> getTokenSubject() {
        return ResponseEntity.ok(tokenInfoService.getTokenSubject());
    }

    // Get all claims
    @GetMapping("/claims")
    public ResponseEntity<Map<String, Object>> getAllClaims() {
        return ResponseEntity.ok(tokenInfoService.getAllClaims());
    }

    @GetMapping("/phone-number")
    public ResponseEntity<?> getPhoneNumber() {
        try {
            String phoneNumber = tokenInfoService.getPhoneNumber();
            return ResponseEntity.ok(Map.of("phoneNumber", phoneNumber));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
} 