package com.aston.stockapp.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class YahooFinanceService {

    private final RestTemplate restTemplate;
    private final YahooResponseConverter converter;
    private static final String API_URL = "https://yahoo-finance15.p.rapidapi.com";
    private static final String API_URL_SECONDARY = "https://apidojo-yahoo-finance-v1.p.rapidapi.com";
    private static final String API_KEY = "f9c5bc36d9mshef13f8f9db483efp19d8cdjsn72b3775c848f";
    private final Map<String, String> tickerCache = new ConcurrentHashMap<>();

    public YahooFinanceService() {
        this.converter = new YahooResponseConverter();
        this.restTemplate = new RestTemplate();
    }

    public String getTickerFromName(String query) {
        // Assumes queries in uppercase and without spaces are tickers
        if (query.matches("^[A-Z]{1,5}$")) {
            log.info("Query recognised as ticker: {}", query);
            // Return the query directly if it matches a ticker pattern
            return query;
        }

        if (tickerCache.containsKey(query)) {
            return tickerCache.get(query);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", API_KEY);
        headers.set("X-RapidAPI-Host", "apidojo-yahoo-finance-v1.p.rapidapi.com");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = API_URL_SECONDARY + "/auto-complete?q=" + query + "&region=US";

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String ticker = converter.getTickerFromResponse(response.getBody());
            // Cache and log the result
            tickerCache.put(query, ticker);
            log.info("Cached new ticker: {} for query: {}", ticker, query);
            return ticker;
        } catch (Exception e) {
            log.error("Error fetching ticker from name: ", e);
        }
        return null;
    }

//    @Async
//    public CompletableFuture<YahooStock> fetchStockDataAsync(String symbol) {
//        return CompletableFuture.supplyAsync(() -> {
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("X-RapidAPI-Key", API_KEY);
//            headers.set("X-RapidAPI-Host", "yahoo-finance15.p.rapidapi.com");
//            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
//            String url = API_URL + "/api/v1/markets/stock/quotes?ticker=" + symbol;
//            ResponseEntity<String> responseStr = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
//            YahooFinanceResponse response = converter.convert(responseStr.getBody());
//            return new YahooStock(response.getSymbol(), response.getLongName(), BigDecimal.valueOf(response.getRegularMarketPrice()), response.getFullExchangeName(), response.getExchange(), response.getRegularMarketVolume(), BigDecimal.valueOf(response.getRegularMarketDayHigh()), BigDecimal.valueOf(response.getRegularMarketDayLow()), BigDecimal.valueOf(response.getMarketCap()), response.isTradeable(), BigDecimal.valueOf(response.getRegularMarketChangePercent()), response.getPreMarketChange(), response.getPreMarketChangePercent(), response.getPreMarketPrice(), response.getPreMarketTime(), response.getPostMarketChange(), response.getPostMarketChangePercent(), response.getPostMarketPrice(), response.getPostMarketTime(), response.getCurrency(), response.getMarketState(), response.getBid(), response.getAsk(), response.getBidSize(), response.getAskSize(), response.getFiftyTwoWeekLow(), response.getFiftyTwoWeekHigh(), response.getTrailingPE(), response.getDividendYield(), response.getEpsTrailingTwelveMonths(), response.getBookValue(), response.getFiftyDayAverage(), response.getTwoHundredDayAverage(), response.getSharesOutstanding(), response.getForwardPE(), response.getPriceToBook(), Integer.valueOf(response.getPriceHint()), Boolean.valueOf(response.isHasPrePostMarketData()));
//        });
//    }
//
//    @Async
//    public CompletableFuture<String> fetchHistoricalDataAsync(String symbol, String range) {
//        return CompletableFuture.supplyAsync(() -> {
//            System.out.println(symbol + " Range: " + range);
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("X-RapidAPI-Key", API_KEY);
//            headers.set("X-RapidAPI-Host", "apidojo-yahoo-finance-v1.p.rapidapi.com");
//            HttpEntity<String> entity = new HttpEntity<>(headers);
//            String url = API_URL_SECONDARY + "/stock/v3/get-chart?interval=1wk&symbol=" + symbol + "&range=" + range + "&region=US";
//            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
//            return response.getBody();
//        });
//    }
//
//    @Async
//    public CompletableFuture<YahooStock> fetchStockInfoAsync(String symbol) {
//        return CompletableFuture.supplyAsync(() -> {
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("X-RapidAPI-Key", API_KEY);
//            headers.set("X-RapidAPI-Host", "apidojo-yahoo-finance-v1.p.rapidapi.com");
//            HttpEntity<String> entity = new HttpEntity<>(headers);
//            String url = API_URL_SECONDARY + "/stock/v3/get-profile" + "?symbol=" + symbol + "&region=US&lang=en-US";
//            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
//            try {
//                return converter.convertProfile(response.getBody());
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//                return null;
//            }
//        });
//    }

    public YahooStock fetchStockData(String symbol) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", API_KEY);
        headers.set("X-RapidAPI-Host", "yahoo-finance15.p.rapidapi.com");
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        String url = API_URL + "/api/v1/markets/stock/quotes?ticker=" + symbol;
        ResponseEntity<String> responseStr = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        YahooFinanceResponse response = converter.convert(responseStr.getBody());
        return new YahooStock(response.getSymbol(), response.getLongName(), BigDecimal.valueOf(response.getRegularMarketPrice()), response.getFullExchangeName(), response.getExchange(), response.getRegularMarketVolume(), BigDecimal.valueOf(response.getRegularMarketDayHigh()), BigDecimal.valueOf(response.getRegularMarketDayLow()), BigDecimal.valueOf(response.getMarketCap()), response.isTradeable(), BigDecimal.valueOf(response.getRegularMarketChangePercent()), response.getPreMarketChange(), response.getPreMarketChangePercent(), response.getPreMarketPrice(), response.getPreMarketTime(), response.getPostMarketChange(), response.getPostMarketChangePercent(), response.getPostMarketPrice(), response.getPostMarketTime(), response.getCurrency(), response.getMarketState(), response.getBid(), response.getAsk(), response.getBidSize(), response.getAskSize(), response.getFiftyTwoWeekLow(), response.getFiftyTwoWeekHigh(), response.getTrailingPE(), response.getDividendYield(), response.getEpsTrailingTwelveMonths(), response.getBookValue(), response.getFiftyDayAverage(), response.getTwoHundredDayAverage(), response.getSharesOutstanding(), response.getForwardPE(), response.getPriceToBook(), Integer.valueOf(response.getPriceHint()), Boolean.valueOf(response.isHasPrePostMarketData()), response.getSector());
    }

    public String fetchHistoricalData(String symbol, String range) {
        System.out.println(symbol + " Range: " + range);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Host", "apidojo-yahoo-finance-v1.p.rapidapi.com");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = API_URL_SECONDARY + "/stock/v3/get-chart?interval=1wk&symbol=" + symbol + "&range=" + range + "&region=US";
        headers.set("X-RapidAPI-Key", API_KEY);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }

    public YahooStock fetchStockInfo(String symbol) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", API_KEY);
        headers.set("X-RapidAPI-Host", "apidojo-yahoo-finance-v1.p.rapidapi.com");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = API_URL_SECONDARY + "/stock/v3/get-profile" + "?symbol=" + symbol + "&region=US&lang=en-US";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        try {
            return converter.convertProfile(response.getBody());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public YahooStock fetchStockNews(String symbol) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", "f9c5bc36d9mshef13f8f9db483efp19d8cdjsn72b3775c848f");
        headers.set("X-RapidAPI-Host", "apidojo-yahoo-finance-v1.p.rapidapi.com");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = API_URL_SECONDARY + "/stock/v3/get-profile" + "?symbol=" + symbol + "&region=US&lang=en-US";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        try {
            System.out.println(response);
            return converter.convertProfile(response.getBody());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

//    public String fetchHistoricalData(String symbol, String range) {
//        System.out.println(symbol + "Range: " + range);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("X-RapidAPI-Key", "f9c5bc36d9mshef13f8f9db483efp19d8cdjsn72b3775c848f");
//        headers.set("X-RapidAPI-Host", "apidojo-yahoo-finance-v1.p.rapidapi.com");
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        String url = API_URL_SECONDARY + "/stock/v3/get-chart?interval=1d&symbol=" + symbol + "&range=" + range + "&region=US&includePrePost=false&useYfid=true&includeAdjustedClose=true&events=capitalGain,div,split";
//        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
//
//        System.out.println(response);
//        return response.getBody();
//    }
}



//    public List<HistoricalPrice> fetchHistoricalStockData(String symbol) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("X-RapidAPI-Key", "f9c5bc36d9mshef13f8f9db483efp19d8cdjsn72b3775c848f");
//        headers.set("X-RapidAPI-Host", "yahoo-finance15.p.rapidapi.com");
//        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
//
//        String url = API_URL + "/v3/get-historical-data" + symbol;
//
//        ResponseEntity<String> responseStr = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
//        YahooFinanceResponse jsonResponse = converter.convert(responseStr.getBody());
//        log.info("Response: {}", responseStr.getBody());
//
//        return null;
//    }

//    public void fetchTrendingTickers() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("X-RapidAPI-Key", "f9c5bc36d9mshef13f8f9db483efp19d8cdjsn72b3775c848f");
//        headers.set("X-RapidAPI-Host", "apidojo-yahoo-finance-v1.p.rapidapi.com");
//        HttpEntity<String> entity = new HttpEntity<>(null, headers);
//
//        String url = "https://apidojo-yahoo-finance-v1.p.rapidapi.com/market/get-trending-tickers";
//
//        ResponseEntity<String> responseStr = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
//        log.info("Response: {}", responseStr.getBody());
//    }


//    public Stock fetchStockData(String symbol) throws JsonProcessingException {
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("X-RapidAPI-Key", "f9c5bc36d9mshef13f8f9db483efp19d8cdjsn72b3775c848f");
//        headers.set("X-RapidAPI-Host", "yahoo-finance15.p.rapidapi.com");
//        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
//
//        String url = API_URL + "/api/v1/markets/stock/quotes?ticker=" + symbol;
//
//        ResponseEntity<String> responseStr = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
//        String body = responseStr.getBody();
//        JsonNode rootNode = objectMapper.readTree(body);
//
//        YahooFinanceResponse jsonResponse = new ObjectMapper().readValue(rootNode.path("body").elements().next().toString(), YahooFinanceResponse.class);
//
//        return new Stock(symbol, jsonResponse.getLongName(), new BigDecimal(jsonResponse.getRegularMarketPrice()));
//    }