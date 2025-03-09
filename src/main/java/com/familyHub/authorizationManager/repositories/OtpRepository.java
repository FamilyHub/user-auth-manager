package com.familyHub.authorizationManager.repositories;

import com.familyHub.authorizationManager.models.Otp;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.Optional;

@Repository
public interface OtpRepository extends MongoRepository<Otp, String> {
    Optional<Otp> findByIdentifierAndTypeAndUsedFalseAndExpiresAtGreaterThan(
        String identifier, 
        String type, 
        Instant now
    );

    void deleteByExpiresAtLessThan(Instant now);
} 