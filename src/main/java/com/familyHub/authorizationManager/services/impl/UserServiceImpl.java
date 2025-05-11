package com.familyHub.authorizationManager.services.impl;

import com.familyHub.authorizationManager.converter.UserConverter;
import com.familyHub.authorizationManager.dto.UserDTO;
import com.familyHub.authorizationManager.exceptions.NotAllowedException;
import com.familyHub.authorizationManager.exceptions.ResourceNotFoundException;
import com.familyHub.authorizationManager.models.User;
import com.familyHub.authorizationManager.repositories.UserRepository;
import com.familyHub.authorizationManager.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserConverter userConverter;

    public UserServiceImpl(UserRepository userRepository, 
                         PasswordEncoder passwordEncoder,
                         UserConverter userConverter) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userConverter = userConverter;
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new NotAllowedException("Email already exists", HttpStatus.NOT_FOUND);
        }

        if (userDTO.getMobileNumber() != null && !userDTO.getMobileNumber().trim().isEmpty()) {
            if (userRepository.existsByMobileNumber(userDTO.getMobileNumber())) {
                throw new NotAllowedException("Mobile number already exists", HttpStatus.NOT_FOUND);
            }
        }

        User user = userConverter.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        User savedUser = userRepository.save(user);
        return userConverter.toDTO(savedUser);
    }

    @Override
    public UserDTO updateUser(String userId, UserDTO userDTO) {
        User existingUser = findUserById(userId);

        if (!existingUser.getEmail().equals(userDTO.getEmail()) && 
            userRepository.existsByEmail(userDTO.getEmail())) {
            throw new NotAllowedException("Email already exists", HttpStatus.NOT_FOUND);
        }

        if (userDTO.getMobileNumber() != null && 
            !userDTO.getMobileNumber().equals(existingUser.getMobileNumber()) && 
            userRepository.existsByMobileNumber(userDTO.getMobileNumber())) {
            throw new NotAllowedException("Mobile number already exists", HttpStatus.NOT_FOUND);
        }

        userConverter.updateEntityFromDTO(existingUser, userDTO);
        if (userDTO.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        User updatedUser = userRepository.save(existingUser);
        return userConverter.toDTO(updatedUser);
    }

    @Override
    public void deleteUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }

    @Override
    public UserDTO getUserById(String userId) {
        User user = findUserById(userId);
        return userConverter.toDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = findUserByEmail(email);
        return userConverter.toDTO(user);
    }

    @Override
    public UserDTO getUserByMobileNumber(String mobileNumber) {
        User user = findUserByMobileNumber(mobileNumber);
        return userConverter.toDTO(user);
    }

    @Override
    public User findUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    @Override
    public User findUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found with email: " + email);
        }
        return user.get();
    }

    @Override
    public User findUserByMobileNumber(String mobileNumber) {
        Optional<User> user = userRepository.findByMobileNumber(mobileNumber);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found with mobile number: " + mobileNumber);
        }
        return user.get();
    }

    @Override
    public List<UserDTO> getAllUsersExcept(String excludedUserId) {
        List<User> users = userRepository.findAll();
        return users.stream()
                .filter(user -> !user.getUserId().equals(excludedUserId))
                .map(user -> {
                    UserDTO dto = new UserDTO();
                    dto.setUserId(user.getUserId());
                    dto.setName(user.getName());
                    dto.setFamilyName(user.getFamilyName());
                    dto.setEmail(user.getEmail());
                    dto.setMobileNumber(user.getMobileNumber());
                    dto.setUserLevel(user.getUserLevel());
                    dto.setRoleTypes(user.getRoles().stream()
                            .map(role -> role.getRoleType().name())
                            .collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }
} 