package com.aston.stockapp.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.converter.Converter;

public class YahooResponseConverter implements Converter<String, YahooFinanceResponse> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public YahooFinanceResponse convert(String json) {
        YahooFinanceResponse response;
        try {
            JsonNode rootNode = objectMapper.readTree(json);
            final String bodyJson = rootNode.path("body").elements().next().toString();
            response = objectMapper.readValue(bodyJson, YahooFinanceResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    public String getTickerFromResponse(String json) {
        try {
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode firstQuote = rootNode.path("quotes").path(0).path("symbol");
            if (firstQuote.isTextual()) {
                return firstQuote.asText();
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing auto-complete response: ", e);
        }
        return null;
    }
}