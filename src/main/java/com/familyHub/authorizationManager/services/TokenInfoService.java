package com.familyHub.authorizationManager.services;

import com.familyHub.authorizationManager.models.User;
import com.familyHub.authorizationManager.security.JwtTokenProvider;
import com.familyHub.authorizationManager.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TokenInfoService {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    public Map<String, Object> getTokenInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        return Map.of(
            "userId", userPrincipal.getId(),
            "email", userPrincipal.getEmail(),
            "name", userPrincipal.getName(),
            "familyName", userPrincipal.getFamilyName(),
            "userLevel", userPrincipal.getUserLevel(),
            "roles", userPrincipal.getAuthorities()
        );
    }

    public String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getId();
    }

    public String getEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getEmail();
    }

    public String getName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getName();
    }

    public String getFamilyName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getFamilyName();
    }

    public String getUserLevel() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getUserLevel();
    }

    public List<?> getRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .toList();
    }

    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userService.findUserById(userPrincipal.getId());
    }

    public String getMobileNumber() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getMobileNumber();
    }

    public List<?> getCustomFields() {
        User user = getUser();
        return user.getCustomFields();
    }

    public Long getTokenExpiration() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = tokenProvider.getTokenFromAuthentication(authentication);
        return tokenProvider.getClaimFromToken(token, "exp", Long.class);
    }

    public Long getTokenIssuedAt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = tokenProvider.getTokenFromAuthentication(authentication);
        return tokenProvider.getClaimFromToken(token, "iat", Long.class);
    }

    public String getTokenSubject() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = tokenProvider.getTokenFromAuthentication(authentication);
        return tokenProvider.getClaimFromToken(token, "sub", String.class);
    }

    public Map<String, Object> getAllClaims() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = tokenProvider.getTokenFromAuthentication(authentication);
        return tokenProvider.getAllClaimsFromToken(token);
    }

    public String getPhoneNumber() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getMobileNumber();
    }
} 