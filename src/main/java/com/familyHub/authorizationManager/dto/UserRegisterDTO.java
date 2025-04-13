package com.familyHub.authorizationManager.dto;

import lombok.Data;
import com.familyHub.authorizationManager.enums.UserLevel;
import java.util.List;


@Data
public class UserRegisterDTO {
    private String userId;
    private String email;
    private String phoneNumber;
    private String name;
    private List<String> roles;
    private UserLevel userLevel;
    private String parentId;
} 