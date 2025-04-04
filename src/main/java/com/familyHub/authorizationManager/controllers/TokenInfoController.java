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

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getTokenInfo() {
        return ResponseEntity.ok(tokenInfoService.getTokenInfo());
    }

    @GetMapping("/user-id")
    public ResponseEntity<String> getUserId() {
        return ResponseEntity.ok(tokenInfoService.getUserId());
    }

    @GetMapping("/email")
    public ResponseEntity<String> getEmail() {
        return ResponseEntity.ok(tokenInfoService.getEmail());
    }

    @GetMapping("/name")
    public ResponseEntity<String> getName() {
        return ResponseEntity.ok(tokenInfoService.getName());
    }

    @GetMapping("/family-name")
    public ResponseEntity<String> getFamilyName() {
        return ResponseEntity.ok(tokenInfoService.getFamilyName());
    }

    @GetMapping("/user-level")
    public ResponseEntity<String> getUserLevel() {
        return ResponseEntity.ok(tokenInfoService.getUserLevel());
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getRoles() {
        return ResponseEntity.ok(tokenInfoService.getRoles());
    }

    @GetMapping("/user")
    public ResponseEntity<User> getUser() {
        return ResponseEntity.ok(tokenInfoService.getUser());
    }
} 