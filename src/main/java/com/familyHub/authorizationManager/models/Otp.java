package com.familyHub.authorizationManager.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.time.Instant;

@Data
@Document(collection = "otps")
public class Otp {
    @Id
    private String id;

    @Field("identifier")
    private String identifier; // email or mobile number

    @Field("otp")
    private String otp;

    @Field("type")
    private String type; // EMAIL or MOBILE

    @Field("created_at")
    private Instant createdAt;

    @Field("expires_at")
    private Instant expiresAt;

    @Field("is_used")
    private boolean used;
} 