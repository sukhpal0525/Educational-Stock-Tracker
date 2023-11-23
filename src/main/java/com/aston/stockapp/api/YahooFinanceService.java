package com.aston.stockapp.api;

import com.aston.stockapp.domain.asset.Stock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Slf4j
@Service
public class YahooFinanceService {

    private final RestTemplate restTemplate;
    private static final String API_URL = "https://yahoo-finance15.p.rapidapi.com";
    private YahooResponseConverter converter;

    public YahooFinanceService() {
        this.converter = new YahooResponseConverter();
        this.restTemplate = new RestTemplate();
    }

    public Stock fetchStockData(String symbol) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", "f9c5bc36d9mshef13f8f9db483efp19d8cdjsn72b3775c848f");
        headers.set("X-RapidAPI-Host", "yahoo-finance15.p.rapidapi.com");
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        String url = API_URL + "/api/v1/markets/stock/quotes?ticker=" + symbol;

        ResponseEntity<String> responseStr = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        YahooFinanceResponse jsonResponse = converter.convert(responseStr.getBody());

        return new Stock(symbol, jsonResponse.getLongName(), BigDecimal.valueOf(jsonResponse.getRegularMarketPrice()));
    }
}




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