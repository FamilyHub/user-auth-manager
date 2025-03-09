package com.familyHub.authorizationManager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.familyHub.authorizationManager.repositories")
@EnableMongoAuditing
public class MongoConfig {
} 