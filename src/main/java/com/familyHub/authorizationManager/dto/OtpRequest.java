package com.familyHub.authorizationManager.dto;

import lombok.Data;

@Data
public class OtpRequest {
    private String email;
    private String mobileNumber;
    private String type; // "EMAIL" or "MOBILE"
} 