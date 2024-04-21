package com.aston.stockapp.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FinancialAPITests {

    @Autowired private MockMvc mockMvc;
    @Autowired private YahooFinanceService yahooFinanceService;

    @Test
    public void testCombinedApiResponses() throws Exception {
        mockMvc.perform(get("/stocks/{symbol}", "AAPL"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("stockData"))
                .andExpect(model().attributeExists("historicalDataJson"))
                .andExpect(model().attributeExists("stockInfo"))
                .andExpect(header().string("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate"))
                .andDo(result -> {
                    ModelAndView modelAndView = result.getModelAndView();
                    assertNotNull(modelAndView);
                    Map<String, Object> modelMap = modelAndView.getModel();
                    assertNotNull(modelMap.get("stockData"));
                    assertNotNull(modelMap.get("historicalDataJson"));
                    assertNotNull(modelMap.get("stockInfo"));

                    // Validations for specific data properties
                    YahooStock stockData = (YahooStock) modelMap.get("stockData");
                    assertNotNull(stockData.getPrice());
                    assertTrue(stockData.getPrice().compareTo(BigDecimal.ZERO) > 0);

                    // Validate specific historical data points
                    String historicalData = (String) modelMap.get("historicalDataJson");
                    assertTrue(historicalData.contains("2010"));

                    // Check for completeness and accuracy of stock information
                    YahooStock stockInfo = (YahooStock) modelMap.get("stockInfo");
                    assertNotNull(stockInfo.getSector());
                    assertNotEquals("", stockInfo.getSector());
                }
            );
    }

//    @Test
//    public void testCombinedApiResponses() throws Exception {
//        mockMvc.perform(get("/stocks/{symbol}", "AAPL"))
//                .andExpect(status().isOk())
//                .andExpect(model().attributeExists("stockData"))
//                .andExpect(model().attributeExists("historicalDataJson"))
//                .andExpect(model().attributeExists("stockInfo"))
//                .andDo(result -> {
//                    ModelAndView modelAndView = result.getModelAndView();
//                    assertNotNull(modelAndView);
//                    Map<String, Object> modelMap = modelAndView.getModel();
//                    assertNotNull(modelMap.get("stockData"));
//                    assertNotNull(modelMap.get("historicalDataJson"));
//                    assertNotNull(modelMap.get("stockInfo"));
//                });
//    }

    @Test
    public void testStockDataFetch() throws Exception {
        mockMvc.perform(get("/finance/stock/{symbol}", "AAPL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").isNumber());
    }

    @Test
    public void testHistoricalDataFetch() throws Exception {
        mockMvc.perform(get("/finance/historical/{symbol}", "AAPL")
                        .param("range", "1y"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    public void testDelayedResponseHandling() throws Exception {
        MvcResult result = mockMvc.perform(get("/finance/stock/{symbol}", "AAPL"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").isNumber())
                .andDo(print());
    }

    @Test
    public void testResponseHeadersForStockData() throws Exception {
        mockMvc.perform(get("/finance/stock/{symbol}", "AAPL"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.price").isNumber());
    }
}
