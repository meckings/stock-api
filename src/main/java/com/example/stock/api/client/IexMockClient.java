package com.example.stock.api.client;

import com.example.stock.api.dto.StockQuote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Service("iexMockClient")
public class IexMockClient implements StockClient{

    private String baseUrl;
    private String token;

    @Autowired
    public IexMockClient(@Value("${iex.url}") String baseUrl, @Value("${iex.token}")String token) {
        this.baseUrl = baseUrl;
        this.token = token;
    }

    @Override
    public <T> Mono<T> getQuote(String symbol, Class<T> tClass) {
        return getQuote(symbol).cast(tClass);
    }

    public Mono<StockQuote> getQuote(String symbol){
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/stock")
                .path("/")
                .path(symbol)
                .path("/quote")
                .queryParam("token", token)
                .build().toUri();
        log.info("HTTP request to {}", uri);
        StockQuote stockQuote = new StockQuote();
        stockQuote.setSymbol(symbol);
        stockQuote.setCalculationPrice("10");
        stockQuote.setIexRealtimePrice(10.0);
        stockQuote.setIexVolume(10L);
        stockQuote.setVolume(10L);
        stockQuote.setAvgTotalVolume(10L);
        stockQuote.setChangePercent(2.5);
        stockQuote.setMarketCap(543345L);
        stockQuote.setCompanyName("Test INC");
        stockQuote.setPrimaryExchange("Test ex");
        return Mono.just(stockQuote);
    }
}
