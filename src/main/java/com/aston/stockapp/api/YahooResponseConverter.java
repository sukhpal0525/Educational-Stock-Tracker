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

    public YahooStock convertProfile(String json) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(json);
        JsonNode summaryProfile = rootNode.path("quoteSummary").path("result").get(0).path("summaryProfile");

        return new YahooStock(
                summaryProfile.path("address1").asText(null),
                summaryProfile.path("city").asText(null),
                summaryProfile.path("state").asText(null),
                summaryProfile.path("zip").asText(null),
                summaryProfile.path("country").asText(null),
                summaryProfile.path("phone").asText(null),
                summaryProfile.path("website").asText(null),
                summaryProfile.path("industry").asText(null),
                summaryProfile.path("sector").asText(null),
                summaryProfile.path("longBusinessSummary").asText(null)
        );
    }
}