package com.familyHub.authorizationManager.models;

import com.familyHub.authorizationManager.enums.UserLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.List;
import java.util.UUID;

@Data
@Document(collection = "users")
@Getter
@Setter
public class User {
    @Id
    private String userId = UUID.randomUUID().toString();

    @Field("name")
    private String name;
    
    @Field("family_name")
    private String familyName;
    
    @Indexed(unique = true)
    @Field("email")
    private String email;
    
    @Indexed(unique = true)
    @Field("mobileNumber")
    private String mobileNumber;
    
    @Field("password")
    private String password;
    
    @Field("roles")
    private List<Role> roles;
    
    @Field("custom_fields")
    private List<CustomField> customFields;
    
    @Field("user_level")
    private UserLevel userLevel;

}