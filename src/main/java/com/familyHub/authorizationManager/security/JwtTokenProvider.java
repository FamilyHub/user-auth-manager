package com.familyHub.authorizationManager.security;

import com.familyHub.authorizationManager.dto.UserRegisterDTO;
import com.familyHub.authorizationManager.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.AuthenticationServiceException;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider implements InitializingBean {

    @Value("${app.security.jwt.secret}")
    private String jwtSecret;

    @Value("${app.security.jwt.expiration}")
    private int jwtExpirationInMs;

    private Key key;

    public JwtTokenProvider() {
        // Initialize with a default key, will be overridden by the secret from properties
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    @Override
    public void afterPropertiesSet() {
        // Use the secret from properties to create the key
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(userPrincipal.getEmail())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .claim("userId", userPrincipal.getId())
                .claim("email", userPrincipal.getEmail())
                .claim("phoneNumber", userPrincipal.getMobileNumber())
                .claim("roles", userPrincipal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .compact();
    }

    public String generateRegistrationToken(UserRegisterDTO userRegisterDTO) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(userRegisterDTO.getEmail())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .claim("userId", userRegisterDTO.getUserId())
                .claim("email", userRegisterDTO.getEmail())
                .claim("phoneNumber", userRegisterDTO.getPhoneNumber())
                .claim("roles", userRegisterDTO.getRoles())
                .claim("userLevel", userRegisterDTO.getUserLevel())
                .compact();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("userId", String.class);
    }

    public <T> T getClaimFromToken(String token, String claimName, Class<T> requiredType) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get(claimName, requiredType);
    }

    public List<GrantedAuthority> getRolesFromToken(String token) {
        @SuppressWarnings("unchecked")
        List<String> roles = getClaimFromToken(token, "roles", List.class);
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            throw new AuthenticationServiceException("Invalid JWT token", ex);
        }
    }

    public String getTokenFromAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationServiceException("No authenticated user found");
        }
        return (String) authentication.getCredentials();
    }

    public Map<String, Object> getAllClaimsFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }

    public String getEmailFromToken(String token) {
        Map<String, Object> claims = getAllClaimsFromToken(token);
        return (String) claims.get("email");
    }

    public String getPhoneNumberFromToken(String token) {
        Map<String, Object> claims = getAllClaimsFromToken(token);
        return (String) claims.get("phoneNumber");
    }
} 