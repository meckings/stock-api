package com.example.stock.api.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DuplicateEntityException extends RuntimeException {

    private String code;
    private List<Error> errors;
    private Throwable throwable;

    public DuplicateEntityException(String message, Throwable throwable) {
        super(message);
        this.throwable = throwable;
    }

    public DuplicateEntityException(String code, String message, Throwable throwable) {
        super(message);
        this.code = code;
        this.throwable = throwable;
    }

    public DuplicateEntityException(String message, List<Error> errors, Throwable throwable) {
        super(message);
        this.errors = errors;
        this.throwable = throwable;
    }
}