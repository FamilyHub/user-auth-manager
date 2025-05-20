package com.familyHub.authorizationManager.controllers;

import com.familyHub.authorizationManager.dto.UserDTO;
import com.familyHub.authorizationManager.security.UserPrincipal;
import com.familyHub.authorizationManager.services.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> regsiterUser(@RequestBody UserDTO userDTO, @RequestHeader(REQUEST_ID_HEADER) String requestId) {
        logger.info("[{}] Registering new user: {}", requestId, userDTO.getEmail());
        UserDTO createdUser = userService.createUser(userDTO);
        logger.info("[{}] User registered successfully: {}", requestId, createdUser.getEmail());
        return ResponseEntity.ok(createdUser);
    }

    @PostMapping("/admin")
    public ResponseEntity<UserDTO> registerAdmin(@RequestBody UserDTO userDTO, @RequestHeader(REQUEST_ID_HEADER) String requestId) {
        logger.info("[{}] Registering new admin user: {}", requestId, userDTO.getEmail());
        UserDTO createdUser = userService.createUser(userDTO);
        logger.info("[{}] Admin user registered successfully: {}", requestId, createdUser.getEmail());
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable String userId,
            @RequestBody UserDTO userDTO,
            @RequestHeader(REQUEST_ID_HEADER) String requestId) {
        logger.info("[{}] Updating user: {}", requestId, userId);
        UserDTO updatedUser = userService.updateUser(userId, userDTO);
        logger.info("[{}] User updated successfully: {}", requestId, userId);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable String userId,
            @RequestHeader(REQUEST_ID_HEADER) String requestId) {
        logger.info("[{}] Deleting user: {}", requestId, userId);
        userService.deleteUser(userId);
        logger.info("[{}] User deleted successfully: {}", requestId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(
            @PathVariable String userId,
            @RequestHeader(REQUEST_ID_HEADER) String requestId) {
        logger.info("[{}] Fetching user by ID: {}", requestId, userId);
        UserDTO user = userService.getUserById(userId);
        logger.info("[{}] User fetched successfully: {}", requestId, userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(
            @PathVariable String email,
            @RequestHeader(REQUEST_ID_HEADER) String requestId) {
        logger.info("[{}] Fetching user by email: {}", requestId, email);
        UserDTO user = userService.getUserByEmail(email);
        logger.info("[{}] User fetched successfully: {}", requestId, email);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(@RequestHeader(REQUEST_ID_HEADER) String requestId) {
        logger.info("[{}] Fetching all users", requestId);
        List<UserDTO> users = userService.getAllUsers();
        logger.info("[{}] Fetched {} users successfully", requestId, users.size());
        return ResponseEntity.ok(users);
    }

    /**
     * Fetches all users except the authenticated user.
     * The authenticated user's ID is extracted from the JWT token in the Authorization header.
     *
     * @return List of all users except the authenticated user
     */
    @GetMapping("/except-me")
    public ResponseEntity<List<UserDTO>> getAllUsersExceptMe(@RequestHeader(REQUEST_ID_HEADER) String requestId) {
        // Get the authenticated user's ID from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String currentUserId = userPrincipal.getId();

        logger.info("[{}] Fetching all users except current user: {}", requestId, currentUserId);
        // Get all users except the current user
        List<UserDTO> users = userService.getAllUsersExcept(currentUserId);
        logger.info("[{}] Fetched {} users successfully", requestId, users.size());
        return ResponseEntity.ok(users);
    }
} 