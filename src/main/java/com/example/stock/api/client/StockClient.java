package com.example.stock.api.client;

import reactor.core.publisher.Mono;

/**
 * The interface Stock client.
 */
public interface StockClient {

    /**
     * Gets quote.
     *
     * @param <T>    the type parameter
     * @param symbol the symbol
     * @param tClass the t class
     * @return the quote
     */
    <T> Mono<T> getQuote(String symbol, Class<T> tClass);
}
