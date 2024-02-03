package com.aston.stockapp.api;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Slf4j
@Service
public class YahooFinanceService {

    private final RestTemplate restTemplate;
    private final YahooResponseConverter converter;
    private static final String API_URL = "https://yahoo-finance15.p.rapidapi.com";
    private static final String AUTO_COMPLETE_API_URL = "https://apidojo-yahoo-finance-v1.p.rapidapi.com";

    public YahooFinanceService() {
        this.converter = new YahooResponseConverter();
        this.restTemplate = new RestTemplate();
    }

    public YahooStock fetchStockData(String symbol) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", "f9c5bc36d9mshef13f8f9db483efp19d8cdjsn72b3775c848f");
        headers.set("X-RapidAPI-Host", "yahoo-finance15.p.rapidapi.com");
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        String url = API_URL + "/api/v1/markets/stock/quotes?ticker=" + symbol;

        ResponseEntity<String> responseStr = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        YahooFinanceResponse jsonResponse = converter.convert(responseStr.getBody());

        return new YahooStock(
                symbol,
                jsonResponse.getLongName(),
                BigDecimal.valueOf(jsonResponse.getRegularMarketPrice()),
                jsonResponse.getFullExchangeName(),
                jsonResponse.getExchange(),
                jsonResponse.getRegularMarketVolume(),
                BigDecimal.valueOf(jsonResponse.getRegularMarketDayHigh()),
                BigDecimal.valueOf(jsonResponse.getRegularMarketDayLow()),
                BigDecimal.valueOf(jsonResponse.getMarketCap()),
                jsonResponse.isTradeable(),
                BigDecimal.valueOf(jsonResponse.getRegularMarketChangePercent()),
                jsonResponse.getPreMarketChange(),
                jsonResponse.getPreMarketChangePercent(),
                jsonResponse.getPreMarketPrice(),
                jsonResponse.getPreMarketTime(),
                jsonResponse.getPostMarketChange(),
                jsonResponse.getPostMarketChangePercent(),
                jsonResponse.getPostMarketPrice(),
                jsonResponse.getPostMarketTime(),
                jsonResponse.getCurrency(),
                jsonResponse.getMarketState(),
                jsonResponse.getBid(),
                jsonResponse.getAsk(),
                jsonResponse.getBidSize(),
                jsonResponse.getAskSize(),
                jsonResponse.getFiftyTwoWeekLow(),
                jsonResponse.getFiftyTwoWeekHigh(),
                jsonResponse.getTrailingPE(),
                jsonResponse.getDividendYield(),
                jsonResponse.getEpsTrailingTwelveMonths(),
                jsonResponse.getBookValue(),
                jsonResponse.getFiftyDayAverage(),
                jsonResponse.getTwoHundredDayAverage(),
                jsonResponse.getSharesOutstanding(),
                jsonResponse.getForwardPE(),
                jsonResponse.getPriceToBook(),
                Integer.valueOf(jsonResponse.getPriceHint())
        );
    }

    public String getTickerFromName(String query) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", "f9c5bc36d9mshef13f8f9db483efp19d8cdjsn72b3775c848f");
        headers.set("X-RapidAPI-Host", "apidojo-yahoo-finance-v1.p.rapidapi.com");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = AUTO_COMPLETE_API_URL + "/auto-complete?q=" + query + "&region=US";

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return converter.getTickerFromResponse(response.getBody());
        } catch (Exception e) {
            log.error("Error fetching ticker from name: ", e);
        }
        return null;
    }

    public String fetchHistoricalData(String symbol) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", "f9c5bc36d9mshef13f8f9db483efp19d8cdjsn72b3775c848f");
        headers.set("X-RapidAPI-Host", "apidojo-yahoo-finance-v1.p.rapidapi.com");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = "https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v3/get-historical-data?symbol=" + symbol + "&region=US";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response.getBody();
    }
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