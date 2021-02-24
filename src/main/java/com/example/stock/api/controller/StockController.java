package com.example.stock.api.controller;

import com.example.stock.api.dto.StockQuote;
import com.example.stock.api.service.StockService;
import com.example.stock.api.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * The Stock controller.
 */
@RestController
@RequestMapping(Constants.API_PREFIX_V1+"/stock")
public class StockController {

    private StockService stockService;

    /**
     * Instantiates a new Stock controller.
     *
     * @param stockService the stock service
     */
    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    /**
     * Get quote for a stock symbol
     *
     * @param symbol the stock dto
     * @return the Mono
     */
    @Operation(description = "Get quote for a stock symbol.")
    @GetMapping
    public Mono<StockQuote> getQuote(@RequestParam String symbol){
        return Mono.create(monoSink -> stockService.getQuote(symbol)
                    .onErrorMap(error->{
                        monoSink.error(error);
                        return error;
                    })
                    .subscribe(monoSink::success)
        );
    }
}
