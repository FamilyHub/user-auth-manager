package com.familyHub.authorizationManager.services;

import com.familyHub.authorizationManager.dto.UserDTO;
import com.familyHub.authorizationManager.models.User;
import java.util.List;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(String userId, UserDTO userDTO);
    void deleteUser(String userId);
    UserDTO getUserById(String userId);
    List<UserDTO> getAllUsers();
    UserDTO getUserByEmail(String email);
    
    // New methods for mobile number
    UserDTO getUserByMobileNumber(String mobileNumber);
    User findUserByMobileNumber(String mobileNumber);
    
    // Existing methods for authentication
    User findUserById(String userId);
    User findUserByEmail(String email);

    /**
     * Gets all users except the specified user.
     *
     * @param excludedUserId The ID of the user to exclude from the results
     * @return List of all users except the specified user
     */
    List<UserDTO> getAllUsersExcept(String excludedUserId);
} 