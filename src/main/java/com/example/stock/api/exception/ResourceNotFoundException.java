package com.example.stock.api.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ResourceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String code;

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String code, String message, Throwable throwable) {
        super(message, throwable);
        this.code = code;
    }
}