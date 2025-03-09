package com.familyHub.authorizationManager.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Document(collection = "authorities")
@Getter
@Setter
public class Authority {

    @Id
    private String id = UUID.randomUUID().toString();

    private String name;

    @Field("user")
    private User user; // Embedded document
}
