package com.familyHub.authorizationManager.models;

import com.familyHub.authorizationManager.enums.RoleType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Data
@Document(collection = "roles")
public class Role {
    @Id
    private String roleId = UUID.randomUUID().toString();

    @Field("role_type")
    private RoleType roleType;
} 