package com.test.ecomm.common.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final String resourceName;
    private final String field;
    private final Object fieldValue;

    public ResourceNotFoundException(String resourceName, String field, Object fieldValue) {
        super(String.format("%s tapılmadı. Parametr: %s, Dəyər: '%s'", resourceName, field, fieldValue));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldValue = fieldValue;
    }
}
