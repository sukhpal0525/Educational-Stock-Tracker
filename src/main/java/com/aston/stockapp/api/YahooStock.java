package com.aston.stockapp.api;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class YahooStock {

    private String ticker;
    private String name;
    private BigDecimal price;

    private String address;
    private String city;
    private String state;
    private String zip;
    private String country;
    private String phone;
    private String website;
    private String industry;
    private String sector;
    private String longBusinessSummary;

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
    private boolean hasPrePostMarketData;

    public YahooStock(String ticker, String name, BigDecimal price, String fullExchangeName, String exchange, long regularMarketVolume, BigDecimal regularMarketDayHigh, BigDecimal regularMarketDayLow, BigDecimal marketCap, boolean tradeable, BigDecimal regularMarketChangePercent, double preMarketChange, double preMarketChangePercent, double preMarketPrice, String preMarketTime, String postMarketChange, double postMarketChangePercent, double postMarketPrice, String postMarketTime, String currency, String marketState, double bid, double ask, int bidSize, int askSize, double fiftyTwoWeekLow, double fiftyTwoWeekHigh, double trailingPE, double dividendYield, double epsTrailingTwelveMonths, double bookValue, double fiftyDayAverage, double twoHundredDayAverage, long sharesOutstanding, double forwardPE, double priceToBook, int priceHint, boolean hasPrePostMarketData) {
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
        this.hasPrePostMarketData = hasPrePostMarketData;
    }

    public YahooStock(String address, String city, String state, String zip, String country, String phone, String website, String industry, String sector, String longBusinessSummary) {
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.country = country;
        this.phone = phone;
        this.website = website;
        this.industry = industry;
        this.sector = sector;
        this.longBusinessSummary = longBusinessSummary;
    }
}