package com.familyHub.authorizationManager.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import com.familyHub.authorizationManager.enums.RoleType;

import java.util.UUID;

@Data
@Document(collection = "roles")
public class Role {
    @Id
    private String roleId = UUID.randomUUID().toString();

    @Field("role_type")
    private RoleType roleType;
} 