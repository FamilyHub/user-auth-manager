package com.familyHub.authorizationManager.services.impl;

import com.familyHub.authorizationManager.dto.UserRegisterDTO;
import com.familyHub.authorizationManager.enums.RoleType;
import com.familyHub.authorizationManager.models.Role;
import com.familyHub.authorizationManager.models.User;
import com.familyHub.authorizationManager.models.UserRegistration;
import com.familyHub.authorizationManager.repositories.UserRegistrationRepository;
import com.familyHub.authorizationManager.repositories.UserRepository;
import com.familyHub.authorizationManager.security.JwtTokenProvider;
import com.familyHub.authorizationManager.services.IUserRegistrationService;
import com.familyHub.authorizationManager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRegistrationServiceImpl implements IUserRegistrationService {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRegistrationRepository userRegistrationRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public String processUserRegistration(UserRegisterDTO userRegisterDTO, Authentication authentication) {
        // Validate parent exists if parentId is provided
        User parent = null;
        if (userRegisterDTO.getParentId() != null && !userRegisterDTO.getParentId().isEmpty()) {
             parent = userService.findUserById(userRegisterDTO.getParentId());
            if (parent == null) {
                throw new IllegalArgumentException("Parent user not found with ID: " + userRegisterDTO.getParentId());
            }
        }

        // Convert string roles to Role entities
        List<Role> roles = userRegisterDTO.getRoles().stream()
                .map(roleStr -> {
                    try {
                        Role role = new Role();
                        role.setRoleType(RoleType.valueOf(roleStr));
                        return role;
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Invalid role: " + roleStr);
                    }
                })
                .collect(Collectors.toList());

        // Create user with converted roles
        User user = new User();
        user.setUserId(userRegisterDTO.getUserId());
        user.setFamilyName(parent.getFamilyName());
        user.setEmail(userRegisterDTO.getEmail());
        user.setMobileNumber(userRegisterDTO.getPhoneNumber());
        user.setName(userRegisterDTO.getName());
        user.setRoles(roles);
        user.setUserLevel(userRegisterDTO.getUserLevel());

        // Generate registration token using the new method
        String token = jwtTokenProvider.generateRegistrationToken(userRegisterDTO);

        // Create and save registration request
        UserRegistration registration = new UserRegistration();
        registration.setUserId(user.getUserId());
        registration.setEmail(user.getEmail());
        registration.setToken(token);
        registration.setRequestTime(new java.util.Date());
        registration.setCompleted(false);


        // Create HTML content for password setup
        String htmlContent = createPasswordSetupHtml(user, token);

        // Send registration email
        emailService.sendRegistrationEmail(user.getEmail(), htmlContent);

        userRegistrationRepository.save(registration);

        userRepository.save(user);
        return token;
    }

    private Authentication createAuthentication(User user) {
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleType().name()))
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                authorities
        );
    }

    private String createPasswordSetupHtml(User user, String token) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <title>Password Setup</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                    }
                    .container {
                        background-color: #f9f9f9;
                        padding: 20px;
                        border-radius: 5px;
                        border: 1px solid #ddd;
                    }
                    .button {
                        display: inline-block;
                        padding: 12px 24px;
                        background-color: #4CAF50;
                        color: white;
                        text-decoration: none;
                        border-radius: 4px;
                        margin: 20px 0;
                    }
                    .button:hover {
                        background-color: #45a049;
                    }
                    .info {
                        margin-top: 20px;
                        padding: 10px;
                        background-color: #e9f7ef;
                        border-radius: 4px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>Welcome to FamilyHub!</h1>
                    <p>Dear %s,</p>
                    <p>Thank you for registering with FamilyHub. To complete your registration, please set up your password by clicking the button below:</p>
                    
                    <a href="http://localhost:3000/api/auth/password/setup?token=%s" class="button">Set Up Password</a>
                    
                    <div class="info">
                        <p><strong>Your registration details:</strong></p>
                        <p>Email: %s</p>
                        <p>Phone: %s</p>
                    </div>
                    
                    <p>If you did not request this registration, please ignore this email.</p>
                    <p>Best regards,<br>The FamilyHub Team</p>
                </div>
            </body>
            </html>
            """, user.getName(), token, user.getEmail(), user.getMobileNumber());
    }
} 