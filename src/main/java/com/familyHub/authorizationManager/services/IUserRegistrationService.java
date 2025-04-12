package com.familyHub.authorizationManager.services;

import org.springframework.security.core.Authentication;
import com.familyHub.authorizationManager.dto.UserRegisterDTO;

public interface IUserRegistrationService {
    String processUserRegistration(UserRegisterDTO userRegisterDTO, Authentication authentication);
} 