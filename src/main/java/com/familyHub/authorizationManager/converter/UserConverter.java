package com.familyHub.authorizationManager.converter;

import com.familyHub.authorizationManager.dto.UserDTO;
import com.familyHub.authorizationManager.models.User;
import com.familyHub.authorizationManager.models.Role;
import com.familyHub.authorizationManager.models.CustomField;
import com.familyHub.authorizationManager.enums.RoleType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserConverter {

    public User toEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        User user = new User();
        user.setName(userDTO.getName());
        user.setFamilyName(userDTO.getFamilyName());
        user.setEmail(userDTO.getEmail());
        user.setMobileNumber(userDTO.getMobileNumber());
        user.setUserLevel(userDTO.getUserLevel());
        
        // Convert role types to Role entities
        if (userDTO.getRoleTypes() != null) {
            List<Role> roles = userDTO.getRoleTypes().stream()
                    .map(roleType -> {
                        Role role = new Role();
                        role.setRoleType(RoleType.valueOf(roleType));
                        return role;
                    })
                    .collect(Collectors.toList());
            user.setRoles(roles);
        }

        // Convert custom fields
        if (userDTO.getCustomFields() != null) {
            List<CustomField> customFields = userDTO.getCustomFields().stream()
                    .map(dto -> {
                        CustomField cf = new CustomField();
                        cf.setFieldName(dto.getFieldName());
                        cf.setFieldValue(dto.getFieldValue());
                        return cf;
                    })
                    .collect(Collectors.toList());
            user.setCustomFields(customFields);
        }

        return user;
    }

    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setFamilyName(user.getFamilyName());
        dto.setEmail(user.getEmail());
        dto.setMobileNumber(user.getMobileNumber());
        dto.setUserLevel(user.getUserLevel());

        // Convert roles to role types
        if (user.getRoles() != null) {
            List<String> roleTypes = user.getRoles().stream()
                    .map(role -> role.getRoleType().name())
                    .collect(Collectors.toList());
            dto.setRoleTypes(roleTypes);
        }

        // Convert custom fields
        if (user.getCustomFields() != null) {
            List<CustomField> customFields = user.getCustomFields().stream()
                    .map(cf -> {
                        CustomField cfDto = new CustomField();
                        cfDto.setFieldName(cf.getFieldName());
                        cfDto.setFieldValue(cf.getFieldValue());
                        return cfDto;
                    })
                    .collect(Collectors.toList());
            dto.setCustomFields(customFields);
        }
        return dto;
    }

    public void updateEntityFromDTO(User user, UserDTO userDTO) {
        if (userDTO.getName() != null) user.setName(userDTO.getName());
        if (userDTO.getFamilyName() != null) user.setFamilyName(userDTO.getFamilyName());
        if (userDTO.getEmail() != null) user.setEmail(userDTO.getEmail());
        if (userDTO.getMobileNumber() != null) user.setMobileNumber(userDTO.getMobileNumber());
        if (userDTO.getUserLevel() != null) user.setUserLevel(userDTO.getUserLevel());
        
        if (userDTO.getRoleTypes() != null) {
            List<Role> roles = userDTO.getRoleTypes().stream()
                    .map(roleType -> {
                        Role role = new Role();
                        role.setRoleType(RoleType.valueOf(roleType));
                        return role;
                    })
                    .collect(Collectors.toList());
            user.setRoles(roles);
        }

        if (userDTO.getCustomFields() != null) {
            List<CustomField> customFields = userDTO.getCustomFields().stream()
                    .map(dto -> {
                        CustomField cf = new CustomField();
                        cf.setFieldName(dto.getFieldName());
                        cf.setFieldValue(dto.getFieldValue());
                        return cf;
                    })
                    .collect(Collectors.toList());
            user.setCustomFields(customFields);
        }
    }
} 