package com.familyHub.authorizationManager.repositories;

import com.familyHub.authorizationManager.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByEmail(String email);
    boolean existsByMobileNumber(String mobileNumber);
    Optional<User> findByEmail(String email);
    User findByMobileNumber(String mobileNumber);
} 