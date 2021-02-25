package com.example.stock.api.service;

import com.example.stock.api.client.StockClient;
import com.example.stock.api.dto.StockDto;
import com.example.stock.api.dto.StockQuote;
import com.example.stock.api.exception.BadRequestException;
import com.example.stock.api.model.Stock;
import com.example.stock.api.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockService {

    private StockClient stockClient;
    private StockRepository stockRepository;

    @Autowired
    public StockService(@Qualifier("iexMockClient") StockClient stockClient, StockRepository stockRepository) {
        this.stockClient = stockClient;
        this.stockRepository = stockRepository;
    }

    public Mono<StockQuote> getQuote(String symbol) {
        return stockClient.getQuote(symbol, StockQuote.class);
    }

    public Mono<List<StockDto>> getStocks() {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext ->
                        stockRepository
                                .findAllByUsername((String)securityContext.getAuthentication().getPrincipal())
                                .collect(Collectors.mapping(StockDto::new, Collectors.toList()))
                );
    }

    public Mono<StockDto> saveStock(StockDto stockDto) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> {
                    stockDto.setUsername((String) securityContext.getAuthentication().getPrincipal());
                    return stockRepository.save(new Stock(stockDto))
                            .map(stock -> {
                                stockDto.setId(stock.getId());
                                return stockDto;
                            });
                })
                .defaultIfEmpty(new StockDto())
                .map(sDto->{
                    if (sDto==null || sDto.getId()==null || sDto.getId().isEmpty()) {
                        throw new BadRequestException("There was an error in processing this request, please try again later.");
                    }
                    return sDto;
                });
    }
}
