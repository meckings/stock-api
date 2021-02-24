package com.example.stock.api.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Service
public class IexClient implements StockClient{

    private String baseUrl;
    private String token;
    private WebClient webClient;

    @Autowired
    public IexClient(@Value("${iex.url}") String baseUrl, @Value("${iex.token}")String token, WebClient webClient) {
        this.baseUrl = baseUrl;
        this.token = token;
        this.webClient = webClient;
    }

    @Override
    public <T> Mono<T> getQuote(String symbol, Class<T> tClass) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/stock")
                .path("/")
                .path(symbol)
                .path("/quote")
                .queryParam("token", token)
                .build().toUri();

        log.info("HTTP request to {}", uri);
        return webClient.get()
                .uri(uri)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(tClass))
                .log();
    }
}
