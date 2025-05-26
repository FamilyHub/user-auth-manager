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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import com.familyHub.authorizationManager.security.UserPrincipal;

import java.util.List;
import java.util.stream.Collectors;

import com.familyHub.authorizationManager.dto.SetUpPasswordRequest;
import com.familyHub.authorizationManager.exceptions.PasswordSetupException;

@Service
public class UserRegistrationServiceImpl implements IUserRegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationServiceImpl.class);

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

    @Autowired
    private AsyncEmailService asyncEmailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Processes user registration with asynchronous email sending.
     * This method handles the complete registration flow including validation,
     * user creation, token generation, and email notification.
     *
     * <p>Asynchronous Email Processing:
     * <ul>
     *     <li>Uses AsyncEmailService for non-blocking email operations</li>
     *     <li>Email sending is executed in a separate thread</li>
     *     <li>Registration process continues without waiting for email completion</li>
     *     <li>Errors in email sending are logged but don't affect registration</li>
     * </ul>
     *
     * <p>Flow:
     * <ol>
     *     <li>Validate parent user if parentId is provided</li>
     *     <li>Convert string roles to Role entities</li>
     *     <li>Create and save new user</li>
     *     <li>Generate registration token</li>
     *     <li>Create registration record</li>
     *     <li>Send registration email asynchronously</li>
     *     <li>Return registration token</li>
     * </ol>
     *
     * @param userRegisterDTO The user registration data
     * @param authentication The current authentication context
     * @return The registration token
     * @throws RuntimeException if registration process fails
     */
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

        /**
         * Asynchronous Email Sending:
         * - The email sending process is started but not waited for
         * - The registration process continues immediately
         * - Any email sending errors are logged but don't affect the registration
         * - The subscribe() method with error callback handles failures
         */
        asyncEmailService.sendRegistrationEmail(user.getEmail(), htmlContent)
            .subscribe(
                null, // No success handler needed
                error -> logger.error("Failed to send registration email", error)
            );

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

    /**
     * Creates an HTML email template for password setup.
     * This method generates a styled HTML email with a password setup link.
     *
     * <p>Template Features:
     * <ul>
     *     <li>Responsive design with CSS styling</li>
     *     <li>Personalized greeting with user's name</li>
     *     <li>Styled button for password setup</li>
     *     <li>Professional footer with team signature</li>
     * </ul>
     *
     * @param user The user to send the email to
     * @param token The registration token for password setup
     * @return Formatted HTML email content
     */
    private String createPasswordSetupHtml(User user, String token) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
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
                    .footer {
                        margin-top: 30px;
                        font-size: 12px;
                        color: #666;
                    }
                </style>
            </head>
            <body>
                <h2>Welcome to FamilyHub!</h2>
                <p>Dear %s,</p>
                <p>Thank you for registering with FamilyHub. To complete your registration, please set up your password by clicking the button below:</p>
                <div style="text-align: center;">
                    <a href="http://localhost:3000/api/auth/password/setup?token=%s" class="button">Set Up Password</a>
                </div>
                <p>If you did not request this registration, please ignore this email.</p>
                <div class="footer">
                    <p>Best regards,<br>The FamilyHub Team</p>
                </div>
            </body>
            </html>
            """, user.getName(), token);
    }

    /**
     * Sets up a new password for a family member.
     * This method handles password validation and secure storage.
     * The userId is extracted from the JWT token in the current security context.
     *
     * <p>Implementation Details:
     * <ul>
     *     <li>Extracts userId from the current security context</li>
     *     <li>Validates password requirements</li>
     *     <li>Checks password confirmation match</li>
     *     <li>Securely hashes the password using BCrypt</li>
     *     <li>Updates user record in database</li>
     * </ul>
     *
     * <p>Error Handling:
     * <ul>
     *     <li>Throws PasswordSetupException for validation failures</li>
     *     <li>Handles database operations in a transaction</li>
     *     <li>Provides detailed error messages</li>
     * </ul>
     *
     * @param request The password setup request
     * @throws PasswordSetupException if password setup fails
     */
    @Override
    @Transactional
    public void setUpFamilyMemberPassword(SetUpPasswordRequest request) {
        try {
            // Get userId from security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new PasswordSetupException("No authenticated user found");
            }

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String userId = userPrincipal.getId();

            // Validate passwords match
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                throw new PasswordSetupException("Passwords do not match");
            }

            // Find user
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new PasswordSetupException("User not found"));

            // Hash and update password
            String hashedPassword = passwordEncoder.encode(request.getNewPassword());
            user.setPassword(hashedPassword);

            // Save updated user
            userRepository.save(user);

            // Log successful password setup
            logger.info("Password successfully set up for user: {}", userId);
        } catch (PasswordSetupException e) {
            logger.error("Password setup failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during password setup: {}", e.getMessage());
            throw new PasswordSetupException("Failed to set up password: " + e.getMessage(), e);
        }
    }
} 