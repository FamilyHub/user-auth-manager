package com.familyHub.authorizationManager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * Web Configuration Class
 * 
 * This class configures CORS (Cross-Origin Resource Sharing) settings for the User Auth Manager service.
 * It defines which origins, methods, and headers are allowed in cross-origin requests.
 * 
 * Key Features:
 * - CORS configuration for API endpoints
 * - Allowed origins configuration
 * - Allowed HTTP methods
 * - Allowed headers
 * - Credentials support
 * 
 * CORS Settings:
 * - Allowed Origins: http://localhost:3000
 * - Allowed Methods: GET, POST, PUT, DELETE, OPTIONS
 * - Allowed Headers: Authorization, Content-Type, X-Requested-With
 * - Allow Credentials: true
 * - Max Age: 3600 seconds (1 hour)
 * 
 * @author Family Hub Team
 * @version 1.0
 */
@Configuration
public class WebConfig {

    /**
     * Creates and configures the CORS web filter for the application.
     * This filter handles cross-origin requests and applies the defined CORS policies.
     * 
     * @return Configured CorsWebFilter instance
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOrigin("http://localhost:3000");
        corsConfig.addAllowedMethod("*");
        corsConfig.addAllowedHeader("*");
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
