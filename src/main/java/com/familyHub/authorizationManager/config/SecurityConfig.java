package com.familyHub.authorizationManager.config;

import com.familyHub.authorizationManager.security.FamilyHubAuthenticationProvider;
import com.familyHub.authorizationManager.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security Configuration Class
 * 
 * This class configures the security settings for the User Auth Manager service.
 * It implements JWT-based authentication and authorization using Spring Security.
 * 
 * Key Features:
 * - JWT-based authentication
 * - Stateless session management
 * - Role-based access control
 * - Protected endpoints configuration
 * - Custom authentication provider
 * 
 * Security Rules:
 * - Public endpoints:
 *   - /actuator/**
 *   - /api/auth/login
 *   - /api/auth/otp/generate
 *   - /api/auth/otp/validate
 *   - /api/users
 *   - /api/auth/register
 *   - /api/auth/refresh
 * - Protected endpoints:
 *   - /api/token/** (requires USER or ADMIN role)
 *   - /api/family-member/** (requires USER or ADMIN role)
 *   - All other endpoints require authentication
 * 
 * @author Family Hub Team
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final FamilyHubAuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthFilter;

    /**
     * Constructs a new SecurityConfig with the required authentication provider and JWT filter.
     * 
     * @param authenticationProvider The custom authentication provider for user authentication
     * @param jwtAuthFilter The JWT authentication filter for token validation
     */
    public SecurityConfig(FamilyHubAuthenticationProvider authenticationProvider, 
                         JwtAuthenticationFilter jwtAuthFilter) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * Configures the security filter chain for the application.
     * Sets up authentication, authorization, and security rules.
     * 
     * @param http The HttpSecurity instance to configure
     * @return Configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/api/auth/login", "/api/auth/otp/generate", "/api/auth/otp/validate", "/api/users", "/api/auth/register", "/api/auth/refresh").permitAll()
                .requestMatchers("/api/token/**","/api/family-member/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Creates and configures the AuthenticationManager bean.
     * This bean is used for authenticating users in the application.
     * 
     * @param config The AuthenticationConfiguration instance
     * @return Configured AuthenticationManager
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
} 