package com.example.stock.api.exception;

import com.example.stock.api.dto.Error;
import com.example.stock.api.dto.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Configuration
@Order(-2)
@Slf4j
public class CustomExceptionResolver implements ErrorWebExceptionHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {

        DataBufferFactory bufferFactory = serverWebExchange.getResponse().bufferFactory();
        ErrorResponse response = new ErrorResponse("400", "A client error has occurred, please check the error(s) below.");
        DataBuffer dataBuffer;
        if (throwable instanceof MethodArgumentNotValidException || throwable instanceof WebExchangeBindException) {
            serverWebExchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            response = new ErrorResponse("400", "Validation failed, check the errors.");
            response.setLogId(serverWebExchange.getRequest().getId());
            List<FieldError> fieldErrors = throwable instanceof MethodArgumentNotValidException ?
                    ((MethodArgumentNotValidException)throwable).getBindingResult().getFieldErrors() :
                    ((WebExchangeBindException)throwable).getBindingResult().getFieldErrors();
            List<Error> errors = fieldErrors.stream()
                    .map(fieldError -> new Error(fieldError.getField(), fieldError.getDefaultMessage()))
                    .collect(Collectors.toList());
            response.setErrors(errors);
            try {
                dataBuffer = bufferFactory.wrap(objectMapper.writeValueAsBytes(response));
            } catch (JsonProcessingException e) {
                dataBuffer = bufferFactory.wrap("".getBytes());
            }
            log.error(String.format("Errors: %s, %s", errors, response.getLogId()));
            serverWebExchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return serverWebExchange.getResponse().writeWith(Mono.just(dataBuffer));
        }

        else if (throwable instanceof BadRequestException || throwable instanceof HttpMessageNotReadableException) {
            serverWebExchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
        }
        else if (throwable instanceof DuplicateEntityException) {
            serverWebExchange.getResponse().setStatusCode(HttpStatus.CONFLICT);
            response.setCode("409");
        }
        else if (throwable instanceof NotFoundException || throwable instanceof HttpClientErrorException.NotFound) {
            serverWebExchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
            response.setCode("404");
        }
        else if (throwable instanceof MethodNotAllowedException) {
            serverWebExchange.getResponse().setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
            response.setCode("405");
        }
        else if (throwable instanceof UnsupportedMediaTypeStatusException) {
            serverWebExchange.getResponse().setStatusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
            response.setCode("415");
        }
        else if (throwable instanceof UnauthorizedException) {
            serverWebExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            response.setCode("401");
        }
        else if (throwable instanceof RuntimeException) {
            serverWebExchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
        }
        try {
            Error error = new Error(serverWebExchange.getRequest().getURI().toString(), throwable.getMessage());
            response.setErrors(Collections.singletonList(error));
            response.setLogId(serverWebExchange.getRequest().getId());
            Throwable cause = throwable.getCause()==null ? throwable : throwable.getCause();
            log.error(String.format("%s, %s, %s", error.getMessage(), error.getFieldName(), response.getLogId()), cause);
            dataBuffer = bufferFactory.wrap(objectMapper.writeValueAsBytes(response));
        } catch (JsonProcessingException e) {
            dataBuffer = bufferFactory.wrap("".getBytes());
        }
        serverWebExchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return serverWebExchange.getResponse().writeWith(Mono.just(dataBuffer));

    }
}
