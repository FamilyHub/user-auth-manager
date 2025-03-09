package com.familyHub.authorizationManager.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Getter
@Setter
public class CustomField {
    @Field("field_name")
    private String fieldName;


    @Field("field_value")
    private String fieldValue;
}