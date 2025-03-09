package com.familyHub.authorizationManager.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "Token")
@Data
public class Token {
    @Id
    private String userId;
    
    @Field("token")
    private String token;
} 