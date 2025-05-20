package com.familyHub.authorizationManager.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter to track request IDs across microservices.
 * This filter:
 * 1. Gets the request ID from the X-Request-ID header
 * 2. If not present, generates a new request ID
 * 3. Adds the request ID to the response headers
 * 4. Logs request start and completion with the request ID
 */
@Component
public class RequestIdFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestIdFilter.class);
    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isEmpty()) {
            requestId = java.util.UUID.randomUUID().toString();
        }

        // Add request ID to response headers
        response.addHeader(REQUEST_ID_HEADER, requestId);

        // Log request start
        logger.info("[{}] Request started: {} {} from {}", 
            requestId,
            request.getMethod(),
            request.getRequestURI(),
            request.getRemoteAddr());

        // Log request headers
        logger.debug("[{}] Request headers: {}", requestId, request.getHeaderNames());

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Log request completion
            logger.info("[{}] Request completed with status: {} {}", 
                requestId,
                response.getStatus(),
                HttpServletResponse.SC_OK == response.getStatus() ? "OK" : "ERROR");

            // Log response headers
            logger.debug("[{}] Response headers: {}", requestId, response.getHeaderNames());
        }
    }
} 