package com.familyHub.authorizationManager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private FamilyHubAuthenticationProvider authenticationProvider;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .csrf((csrf) -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()  // Allow user registration
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()  // Allow login
                .requestMatchers(HttpMethod.POST, "/api/auth/otp/generate").permitAll()  // Allow OTP generation
                .requestMatchers(HttpMethod.POST, "/api/auth/otp/validate").permitAll()  // Allow OTP validation
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider)  // Use custom authentication provider
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

        return httpSecurity.build();
    }
} 