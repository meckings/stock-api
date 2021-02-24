package com.example.stock.api.model;

import com.example.stock.api.dto.StockDto;
import com.example.stock.api.dto.StockQuote;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ToString
@Document
@NoArgsConstructor
@AllArgsConstructor
public class Stock implements Serializable {

    @Id
    private String id;
    @Indexed
    private String username;
    private String symbol;
    private String companyName;
    private String primaryExchange;
    private Double priceBought;
    private Double amountPaid;
    private Double volumeBought;

    public Stock(StockDto stock){
        this.username = stock.getUsername();
        this.symbol = stock.getSymbol();
        this.companyName = stock.getCompanyName();
        this.primaryExchange = stock.getPrimaryExchange();
        this.priceBought = stock.getPriceBought();
        this.amountPaid = stock.getAmountPaid();
        this.volumeBought = stock.getVolumeBought();
    }
}
