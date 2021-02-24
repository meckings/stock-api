package com.example.stock.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;

import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
public class WebClientConfig {

    @Bean
    public WebClient webClient(){
        return WebClient
                .builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()))
                .filter(ExchangeFilterFunctions.statusError(HttpStatus::is5xxServerError, (clientResponse -> new WebClientResponseException(clientResponse.statusCode().value(),
                            clientResponse.statusCode().getReasonPhrase(),
                            clientResponse.headers().asHttpHeaders(), null, StandardCharsets.UTF_8)
                )))
                .build();
    }
}
