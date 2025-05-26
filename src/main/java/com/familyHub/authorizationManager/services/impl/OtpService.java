package com.familyHub.authorizationManager.services.impl;

import com.familyHub.authorizationManager.models.Otp;
import com.familyHub.authorizationManager.models.User;
import com.familyHub.authorizationManager.repositories.OtpRepository;
import com.familyHub.authorizationManager.services.UserService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.Instant;
import java.util.Random;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OtpService {
    
    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);
    private static final long OTP_VALID_DURATION = 5 * 60; // 5 minutes in seconds

    @Autowired
    private UserService userService;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    public String generateOtp(String identifier, String type) {
        logger.debug("Generating OTP for identifier: {} of type: {}", identifier, type);
        
        // Check if user exists based on type
        User user = null;
        if ("EMAIL".equalsIgnoreCase(type)) {
            user = userService.findUserByEmail(identifier);
        } else if ("MOBILE".equalsIgnoreCase(type)) {
            user = userService.findUserByMobileNumber(identifier);
        } else {
            throw new IllegalArgumentException("Invalid OTP type. Must be either EMAIL or MOBILE");
        }

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Generate 6-digit OTP
        String otpValue = String.format("%06d", new Random().nextInt(999999));
        
        // Create new OTP entity
        Otp otp = new Otp();
        otp.setIdentifier(identifier);
        otp.setOtp(otpValue);
        otp.setType(type.toUpperCase());
        otp.setCreatedAt(Instant.now());
        otp.setExpiresAt(Instant.now().plusSeconds(OTP_VALID_DURATION));
        otp.setUsed(false);
        
        // Save to database
        otpRepository.save(otp);
        
        try {
            if ("EMAIL".equalsIgnoreCase(type)) {
                emailService.sendOtpEmail(identifier, otpValue);
            } else {
                smsService.sendOtpSms(identifier, otpValue);
            }
            logger.info("OTP sent successfully to {} via {}", identifier, type);
        } catch (Exception e) {
            logger.error("Failed to send OTP: {}", e.getMessage(), e);
            // Still return the OTP for development/testing
            logger.debug("Development mode - OTP for {} {}: {}", type, identifier, otpValue);
        }
        
        return otpValue;
    }

    public boolean validateOtp(String identifier, String otpValue, String type) {
        logger.debug("Validating OTP for identifier: {} of type: {}", identifier, type);
        
        if ("MOBILE".equalsIgnoreCase(type)) {
            // For mobile, use Twilio's Verify API
            return smsService.verifyOtp(identifier, otpValue);
        }
        
        // For email, use our database validation
        Optional<Otp> latestOtp = otpRepository.findByIdentifierAndTypeAndUsedFalseAndExpiresAtGreaterThan(
            identifier,
            type.toUpperCase(),
            Instant.now()
        );

        if (latestOtp.isEmpty()) {
            logger.debug("No valid OTP found for identifier: {} and type: {}", identifier, type);
            return false;
        }

        Otp otp = latestOtp.get();
        logger.debug("Found OTP entry: createdAt={}, expiresAt={}, used={}", 
            otp.getCreatedAt(), otp.getExpiresAt(), otp.isUsed());

        if (!otp.getOtp().equals(otpValue)) {
            logger.debug("OTP value mismatch for identifier: {}", identifier);
            return false;
        }

        if (otp.getExpiresAt().isBefore(Instant.now())) {
            logger.debug("OTP expired for identifier: {}. Expiry time: {}", identifier, otp.getExpiresAt());
            return false;
        }

        try {
            otp.setUsed(true);
            otpRepository.save(otp);
            logger.debug("OTP validated successfully for identifier: {}", identifier);
            return true;
        } catch (Exception e) {
            logger.error("Error while marking OTP as used: {}", e.getMessage(), e);
            return false;
        }
    }

    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    public void cleanupExpiredOtps() {
        logger.debug("Running scheduled cleanup of expired OTPs");
        otpRepository.deleteByExpiresAtLessThan(Instant.now());
    }
} 