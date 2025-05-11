package com.familyHub.authorizationManager.repositories;

import com.familyHub.authorizationManager.models.UserRegistration;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRegistrationRepository extends MongoRepository<UserRegistration, String> {
    Optional<UserRegistration> findByEmail(String email);
    Optional<UserRegistration> findByToken(String token);
} 