package com.familyHub.authorizationManager.dto;

import com.familyHub.authorizationManager.enums.UserLevel;
import com.familyHub.authorizationManager.models.CustomField;
import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserLevel getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(UserLevel userLevel) {
        this.userLevel = userLevel;
    }

    public List<String> getRoleTypes() {
        return roleTypes;
    }

    public void setRoleTypes(List<String> roleTypes) {
        this.roleTypes = roleTypes;
    }

    public List<CustomField> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(List<CustomField> customFields) {
        this.customFields = customFields;
    }

    private String name;
    private String familyName;
    private String email;
    private String mobileNumber;
    private String password;
    private UserLevel userLevel;
    private List<String> roleTypes;
    private List<CustomField> customFields;
}

