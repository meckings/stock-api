package com.example.stock.api.controller;

import com.example.stock.api.dto.StockDto;
import com.example.stock.api.dto.StockQuote;
import com.example.stock.api.service.StockService;
import com.example.stock.api.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

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
        return stockService.getQuote(symbol);
    }

    /**
     * Get all the stocks bought by a logged in user
     *
     * @return the Mono
     */
    @Operation(description = "Get all the stocks bought by a logged in user.")
    @GetMapping("/me")
    public Mono<List<StockDto>> getStock(){
        return stockService.getStocks();
    }

    /**
     * Save stock that has been bought
     *
     * @param stockDto the stock dto
     * @return the Mono
     */
    @Operation(description = "Save stock that has been bought.")
    @PostMapping
    public Mono<StockDto> saveStock(@RequestBody @Validated StockDto stockDto){
        return stockService.saveStock(stockDto);
    }
}
