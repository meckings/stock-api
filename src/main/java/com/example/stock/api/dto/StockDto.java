package com.example.stock.api.dto;

import com.example.stock.api.model.Stock;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StockDto implements Serializable {

    private String id;
    private String username;
    @NotEmpty(message = "symbol cannot be empty")
    private String symbol;
    @NotEmpty(message = "companyName cannot be empty")
    private String companyName;
    @NotEmpty(message = "primaryExchange cannot be empty")
    private String primaryExchange;
    @NotNull(message = "priceBought cannot be null")
    private Double priceBought;
    @NotNull(message = "amountPaid cannot be null")
    private Double amountPaid;
    @NotNull(message = "volumeBought cannot be empty")
    private Double volumeBought;

    public StockDto(Stock stock){
        this.id = stock.getId();
        this.username = stock.getUsername();
        this.symbol = stock.getSymbol();
        this.companyName = stock.getCompanyName();
        this.primaryExchange = stock.getPrimaryExchange();
        this.priceBought = stock.getPriceBought();
        this.amountPaid = stock.getAmountPaid();
        this.volumeBought = stock.getVolumeBought();
    }
}
