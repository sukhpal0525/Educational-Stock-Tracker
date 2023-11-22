package com.aston.stockapp.domain.asset;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Stock {

    private String ticker;
    private String name;
    private BigDecimal price;

    public Stock(String ticker, String name, BigDecimal price) {
        this.ticker = ticker;
        this.name = name;
        this.price = price;
    }
}