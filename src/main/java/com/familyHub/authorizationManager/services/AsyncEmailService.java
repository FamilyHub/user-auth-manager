package com.familyHub.authorizationManager.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import jakarta.mail.internet.MimeMessage;

/**
 * Service for handling asynchronous email operations using Spring WebFlux.
 * This service provides non-blocking email sending capabilities to improve application
 * performance and user experience.
 *
 * <p>Key Features:
 * <ul>
 *     <li>Asynchronous email sending using @Async annotation</li>
 *     <li>Reactive programming with WebFlux's Mono</li>
 *     <li>HTML email support with MimeMessage</li>
 *     <li>Non-blocking operation execution</li>
 * </ul>
 *
 * <p>Technical Implementation Details:
 * <ul>
 *     <li>Uses Spring's JavaMailSender for email operations</li>
 *     <li>Leverages Spring's @Async for asynchronous execution</li>
 *     <li>Implements reactive programming with Mono for better resource utilization</li>
 *     <li>Provides error handling through reactive streams</li>
 * </ul>
 *
 * @author FamilyHub Team
 * @version 1.0
 * @since 2024
 */
@Service
public class AsyncEmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Sends a registration email asynchronously using Spring WebFlux.
     * This method creates and sends an HTML email in a non-blocking manner.
     *
     * <p>Implementation Details:
     * <ul>
     *     <li>Creates a MimeMessage for HTML content support</li>
     *     <li>Uses MimeMessageHelper for easy message configuration</li>
     *     <li>Executes email sending in a separate thread</li>
     *     <li>Returns a Mono<Void> for reactive stream handling</li>
     * </ul>
     *
     * <p>Error Handling:
     * <ul>
     *     <li>Wraps email sending in a try-catch block</li>
     *     <li>Throws RuntimeException with original exception for error propagation</li>
     *     <li>Errors are handled by the subscriber's error callback</li>
     * </ul>
     *
     * @param to The recipient's email address
     * @param htmlContent The HTML content of the email
     * @return Mono<Void> representing the asynchronous email sending operation
     * @throws RuntimeException if email sending fails
     */
    @Async
    public Mono<Void> sendRegistrationEmail(String to, String htmlContent) {
        return Mono.fromRunnable(() -> {
            try {
                // Create a MimeMessage for HTML content
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                
                // Configure email properties
                helper.setTo(to);
                helper.setSubject("Complete Your FamilyHub Registration");
                helper.setText(htmlContent, true); // true indicates HTML content
                
                // Send email asynchronously
                mailSender.send(message);
            } catch (Exception e) {
                throw new RuntimeException("Failed to send email", e);
            }
        });
    }
} 