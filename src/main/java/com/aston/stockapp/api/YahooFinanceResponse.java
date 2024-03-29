package com.aston.stockapp.api;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class YahooFinanceResponse {

    private double preMarketChange;
    private double preMarketChangePercent;
    private double preMarketPrice;
    private String preMarketTime;
    private String postMarketChange;
    private double postMarketChangePercent;
    private double postMarketPrice;
    private String postMarketTime;
    private String language;
    private String region;
    private String quoteType;
    private String typeDisp;
    private String quoteSourceName;
    private boolean triggerable;
    private String customPriceAlertConfidence;
    private String currency;
    private String marketState;
    private double regularMarketChangePercent;
    private double regularMarketPrice;
    private String exchange;
    private String shortName;
    private String longName;
    private String messageBoardId;
    private String exchangeTimezoneName;
    private String exchangeTimezoneShortName;
    private int gmtOffSetMilliseconds;
    private String market;
    private boolean esgPopulated;
    private long firstTradeDateMilliseconds;
    private int priceHint;
    private double regularMarketChange;
    private int regularMarketTime;
    private double regularMarketDayHigh;
    private String regularMarketDayRange;
    private double regularMarketDayLow;
    private int regularMarketVolume;
    private double regularMarketPreviousClose;
    private double bid;
    private double ask;
    private int bidSize;
    private int askSize;
    private String fullExchangeName;
    private String financialCurrency;
    private double regularMarketOpen;
    private int averageDailyVolume3Month;
    private int averageDailyVolume10Day;
    private double fiftyTwoWeekLowChange;
    private double fiftyTwoWeekLowChangePercent;
    private String fiftyTwoWeekRange;
    private double fiftyTwoWeekHighChange;
    private double fiftyTwoWeekHighChangePercent;
    private double fiftyTwoWeekLow;
    private double fiftyTwoWeekHigh;
    private double fiftyTwoWeekChangePercent;
    private int dividendDate;
    private int earningsTimestamp;
    private int earningsTimestampStart;
    private int earningsTimestampEnd;
    private double trailingAnnualDividendRate;
    private double trailingPE;
    private double dividendRate;
    private double trailingAnnualDividendYield;
    private double dividendYield;
    private double epsTrailingTwelveMonths;
    private double epsForward;
    private double epsCurrentYear;
    private double priceEpsCurrentYear;
    private long sharesOutstanding;
    private double bookValue;
    private double fiftyDayAverage;
    private double fiftyDayAverageChange;
    private double fiftyDayAverageChangePercent;
    private double twoHundredDayAverage;
    private double twoHundredDayAverageChange;
    private double twoHundredDayAverageChangePercent;
    private long marketCap;
    private double forwardPE;
    private double priceToBook;
    private int sourceInterval;
    private int exchangeDataDelayedBy;
    private String averageAnalystRating;
    private boolean tradeable;
    private boolean cryptoTradeable;
    private String displayName;
    private String symbol;
    private boolean hasPrePostMarketData;
    private String sector;
}