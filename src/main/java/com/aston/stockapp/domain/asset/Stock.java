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
    private String exchange;
    private long regularMarketVolume;
    private BigDecimal regularMarketDayHigh;
    private BigDecimal regularMarketDayLow;
    private BigDecimal marketCap;
    private boolean tradeable;
    private BigDecimal regularMarketChangePercent;

    private double preMarketChange;
    private double preMarketChangePercent;
    private double preMarketPrice;
    private String preMarketTime;
    private String postMarketChange;
    private double postMarketChangePercent;
    private double postMarketPrice;
    private String postMarketTime;
    private String currency;
    private String marketState;
    private double bid;
    private double ask;
    private int bidSize;
    private int askSize;
    private double fiftyTwoWeekLow;
    private double fiftyTwoWeekHigh;
    private double trailingPE;
    private double dividendYield;
    private double epsTrailingTwelveMonths;
    private double bookValue;
    private double fiftyDayAverage;
    private double twoHundredDayAverage;
    private long sharesOutstanding;
    private double forwardPE;
    private double priceToBook;
    private int priceHint;

    public Stock(String ticker, String name, BigDecimal price, String fullExchangeName, String exchange, long regularMarketVolume, BigDecimal regularMarketDayHigh, BigDecimal regularMarketDayLow, BigDecimal marketCap, boolean tradeable, BigDecimal regularMarketChangePercent, double preMarketChange, double preMarketChangePercent, double preMarketPrice, String preMarketTime, String postMarketChange, double postMarketChangePercent, double postMarketPrice, String postMarketTime, String currency, String marketState, double bid, double ask, int bidSize, int askSize, double fiftyTwoWeekLow, double fiftyTwoWeekHigh, double trailingPE, double dividendYield, double epsTrailingTwelveMonths, double bookValue, double fiftyDayAverage, double twoHundredDayAverage, long sharesOutstanding, double forwardPE, double priceToBook, int priceHint) {
        this.ticker = ticker;
        this.name = name;
        this.price = price;
        this.fullExchangeName = fullExchangeName;
        this.exchange = exchange;
        this.regularMarketVolume = regularMarketVolume;
        this.regularMarketDayHigh = regularMarketDayHigh;
        this.regularMarketDayLow = regularMarketDayLow;
        this.marketCap = marketCap;
        this.tradeable = tradeable;
        this.regularMarketChangePercent = regularMarketChangePercent;
        this.preMarketChange = preMarketChange;
        this.preMarketChangePercent = preMarketChangePercent;
        this.preMarketPrice = preMarketPrice;
        this.preMarketTime = preMarketTime;
        this.postMarketChange = postMarketChange;
        this.postMarketChangePercent = postMarketChangePercent;
        this.postMarketPrice = postMarketPrice;
        this.postMarketTime = postMarketTime;
        this.currency = currency;
        this.marketState = marketState;
        this.bid = bid;
        this.ask = ask;
        this.bidSize = bidSize;
        this.askSize = askSize;
        this.fiftyTwoWeekLow = fiftyTwoWeekLow;
        this.fiftyTwoWeekHigh = fiftyTwoWeekHigh;
        this.trailingPE = trailingPE;
        this.dividendYield = dividendYield;
        this.epsTrailingTwelveMonths = epsTrailingTwelveMonths;
        this.bookValue = bookValue;
        this.fiftyDayAverage = fiftyDayAverage;
        this.twoHundredDayAverage = twoHundredDayAverage;
        this.sharesOutstanding = sharesOutstanding;
        this.forwardPE = forwardPE;
        this.priceToBook = priceToBook;
        this.priceHint = priceHint;
    }
}