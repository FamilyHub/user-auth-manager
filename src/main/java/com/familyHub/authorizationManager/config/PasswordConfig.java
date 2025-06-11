package com.familyHub.authorizationManager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Password Configuration Class
 * 
 * This class provides password encoding configuration for the User Auth Manager service.
 * It uses BCrypt algorithm for secure password hashing.
 * 
 * Key Features:
 * - BCrypt password encoding
 * - Configurable strength (default: 10)
 * - Secure password storage
 * 
 * Usage:
 * - Password encoding during user registration
 * - Password verification during authentication
 * - Password updates
 * 
 * Security Considerations:
 * - Uses BCrypt's adaptive hashing algorithm
 * - Includes salt automatically
 * - Resistant to rainbow table attacks
 * 
 * @author Family Hub Team
 * @version 1.0
 */
@Configuration
public class PasswordConfig {

    /**
     * Creates and configures the PasswordEncoder bean using BCrypt.
     * The default strength of 10 provides a good balance between security and performance.
     * 
     * @return BCryptPasswordEncoder instance for password hashing
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 