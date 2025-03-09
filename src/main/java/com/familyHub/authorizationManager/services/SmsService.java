package com.familyHub.authorizationManager.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Value("${twilio.account.sid}")
    private String ACCOUNT_SID;

    @Value("${twilio.auth.token}")
    private String AUTH_TOKEN;

    @Value("${twilio.verify.service.sid}")
    private String SERVICE_SID;

    public void sendOtpSms(String toPhoneNumber, String otp) {
        try {
            // Initialize Twilio with credentials
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

            // Format the phone number if it doesn't start with +
            String formattedPhoneNumber = toPhoneNumber.startsWith("+") ? toPhoneNumber : "+" + toPhoneNumber;

            // Create and send the message
            Message message = Message.creator(
                new PhoneNumber(formattedPhoneNumber),
                SERVICE_SID,
                "Your FamilyHub OTP is: " + otp + ". This OTP will expire in 5 minutes."
            ).create();

            System.out.println("SMS sent with SID: " + message.getSid());
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP SMS: " + e.getMessage(), e);
        }
    }
} 