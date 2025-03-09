package com.familyHub.authorizationManager.services;

import com.familyHub.authorizationManager.models.Otp;
import com.familyHub.authorizationManager.models.User;
import com.familyHub.authorizationManager.repositories.OtpRepository;
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
        } catch (Exception e) {
            // Log the error but don't expose it to the user
            System.err.println("Failed to send OTP: " + e.getMessage());
            e.printStackTrace();
            // Still return the OTP for development/testing
            System.out.println("OTP for " + type + " " + identifier + ": " + otpValue);
        }
        
        return otpValue;
    }

    public boolean validateOtp(String identifier, String otpValue, String type) {
        logger.debug("Starting OTP validation for identifier: {}, type: {}", identifier, type);
        
        // Step 1: Find the latest valid OTP
        Optional<Otp> latestOtp = otpRepository.findByIdentifierAndTypeAndUsedFalseAndExpiresAtGreaterThan(
            identifier,
            type.toUpperCase(),
            Instant.now()
        );

        // Step 2: Check if OTP exists
        if (latestOtp.isEmpty()) {
            logger.debug("No valid OTP found for identifier: {} and type: {}", identifier, type);
            return false;
        }

        // Step 3: Get the OTP entity
        Otp otp = latestOtp.get();
        logger.debug("Found OTP entry: createdAt={}, expiresAt={}, used={}", 
            otp.getCreatedAt(), otp.getExpiresAt(), otp.isUsed());

        // Step 4: Validate OTP value
        if (!otp.getOtp().equals(otpValue)) {
            logger.debug("OTP value mismatch for identifier: {}", identifier);
            return false;
        }

        // Step 5: Check expiration
        if (otp.getExpiresAt().isBefore(Instant.now())) {
            logger.debug("OTP expired for identifier: {}. Expiry time: {}", identifier, otp.getExpiresAt());
            return false;
        }

        // Step 6: Mark OTP as used
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

    // Clean up expired OTPs every hour
    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    public void cleanupExpiredOtps() {
        otpRepository.deleteByExpiresAtLessThan(Instant.now());
    }
} 