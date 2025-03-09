package com.familyHub.authorizationManager.dto;

import lombok.Data;

@Data
public class OtpValidationRequest {
    private String email;
    private String mobileNumber;
    private String type; // "EMAIL" or "MOBILE"
    private String otp;
} 