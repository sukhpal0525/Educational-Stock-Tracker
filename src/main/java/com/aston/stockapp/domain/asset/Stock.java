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
    private String fullExchangeName;
    private long regularMarketVolume;
    private BigDecimal regularMarketDayHigh;
    private BigDecimal regularMarketDayLow;
    private BigDecimal marketCap;
    private boolean tradeable;
    private BigDecimal regularMarketChangePercent;

    public Stock(String ticker, String name, BigDecimal price, String fullExchangeName, long regularMarketVolume, BigDecimal regularMarketDayHigh,BigDecimal regularMarketDayLow, BigDecimal marketCap, boolean tradeable, BigDecimal regularMarketChangePercent) {
        this.ticker = ticker;
        this.name = name;
        this.price = price;
        this.fullExchangeName = fullExchangeName;
        this.regularMarketVolume = regularMarketVolume;
        this.regularMarketDayHigh = regularMarketDayHigh;
        this.regularMarketDayLow = regularMarketDayLow;
        this.marketCap = marketCap;
        this.tradeable = tradeable;
        this.regularMarketChangePercent = regularMarketChangePercent;
    }
}