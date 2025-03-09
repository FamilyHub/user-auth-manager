package com.familyHub.authorizationManager.security;

import com.familyHub.authorizationManager.exceptions.AuthenticationException;
import com.familyHub.authorizationManager.models.User;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${app.security.jwt.secret}")
    private String jwtSecret;

    @Value("${app.security.jwt.expiration}")
    private long jwtExpirationInMs;

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(user.getUserId())
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .claim("familyName", user.getFamilyName())
                .claim("userLevel", user.getUserLevel())
                .claim("roles", user.getRoles())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            throw new AuthenticationException("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            throw new AuthenticationException("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            throw new AuthenticationException("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            throw new AuthenticationException("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            throw new AuthenticationException("JWT claims string is empty");
        }
    }
} 