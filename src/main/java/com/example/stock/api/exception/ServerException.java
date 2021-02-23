package com.example.stock.api.exception;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServerException extends RuntimeException {

    private String code;
    private List<Error> errors;

    public ServerException(String message, List<Error> errors, Throwable throwable) {
        super(message, throwable);
        this.errors = errors;
    }

    public ServerException(String code, String message, Throwable throwable) {
        super(message, throwable);
        this.code = code;
    }

    public ServerException(String message, Throwable throwable) {
        super(message, throwable);
    }
}