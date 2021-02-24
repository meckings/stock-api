package com.example.stock.api.service;

import com.example.stock.api.client.StockClient;
import com.example.stock.api.dto.StockQuote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class StockService {

    private StockClient stockClient;

    @Autowired
    public StockService(StockClient stockClient) {
        this.stockClient = stockClient;
    }

    public Mono<StockQuote> getQuote(String symbol){
        return stockClient.getQuote(symbol, StockQuote.class);
    }
}
