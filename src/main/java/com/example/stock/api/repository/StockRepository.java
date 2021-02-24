package com.example.stock.api.repository;

import com.example.stock.api.model.Stock;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface StockRepository extends ReactiveMongoRepository<Stock, String> {

    Flux<Stock> findAllByUsername(String username);
}
