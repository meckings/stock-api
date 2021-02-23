package com.example.stock.api.exception;


import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}