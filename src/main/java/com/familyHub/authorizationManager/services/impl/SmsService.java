package com.familyHub.authorizationManager.services.impl;

import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SmsService {
    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);
    private static final String INDIA_COUNTRY_CODE = "+91";

    @Value("${twilio.account.sid}")
    private String ACCOUNT_SID;

    @Value("${twilio.auth.token}")
    private String AUTH_TOKEN;

    @Value("${twilio.verify.service.sid}")
    private String VERIFY_SERVICE_SID;

    private String formatIndianPhoneNumber(String phoneNumber) {
        // Remove any spaces, dashes, or other characters
        String cleanNumber = phoneNumber.replaceAll("[^0-9]", "");
        
        // If number already starts with 91, add +
        if (cleanNumber.startsWith("91")) {
            return "+" + cleanNumber;
        }
        
        // If number starts with 0, remove it and add +91
        if (cleanNumber.startsWith("0")) {
            return INDIA_COUNTRY_CODE + cleanNumber.substring(1);
        }
        
        // If 10 digits, add +91
        if (cleanNumber.length() == 10) {
            return INDIA_COUNTRY_CODE + cleanNumber;
        }
        
        // If already has +, return as is
        if (phoneNumber.startsWith("+")) {
            return phoneNumber;
        }
        
        throw new IllegalArgumentException("Invalid Indian phone number format");
    }

    public void sendOtpSms(String toPhoneNumber, String otp) {
        try {
            logger.debug("Initializing Twilio with Account SID: {}", ACCOUNT_SID);
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

            // Format the Indian phone number
            String formattedPhoneNumber = formatIndianPhoneNumber(toPhoneNumber);
            logger.debug("Formatted phone number: {}", formattedPhoneNumber);
            
            logger.debug("Sending verification to phone number: {}", formattedPhoneNumber);
            
            // Create verification
            Verification verification = Verification.creator(
                    VERIFY_SERVICE_SID,
                    formattedPhoneNumber,
                    "sms")
                .create();

            logger.info("Verification status: {}", verification.getStatus());
            
            // For development/testing, log the OTP
            logger.debug("Development mode - OTP value: {}", otp);
            
        } catch (Exception e) {
            logger.error("Failed to send OTP SMS: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send OTP SMS: " + e.getMessage(), e);
        }
    }

    public boolean verifyOtp(String phoneNumber, String otp) {
        try {
            // Format the Indian phone number
            String formattedPhoneNumber = formatIndianPhoneNumber(phoneNumber);
            logger.debug("Verifying OTP for formatted phone number: {}", formattedPhoneNumber);
            
            // Check verification
            VerificationCheck verificationCheck = VerificationCheck.creator(
                    VERIFY_SERVICE_SID)
                .setTo(formattedPhoneNumber)
                .setCode(otp)
                .create();

            logger.info("Verification check status: {}", verificationCheck.getStatus());
            
            return "approved".equals(verificationCheck.getStatus());
        } catch (Exception e) {
            logger.error("Failed to verify OTP: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to verify OTP: " + e.getMessage(), e);
        }
    }
} 