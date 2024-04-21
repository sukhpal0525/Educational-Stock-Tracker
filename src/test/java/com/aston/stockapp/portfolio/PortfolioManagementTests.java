package com.aston.stockapp.portfolio;

import com.aston.stockapp.domain.portfolio.PortfolioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PortfolioManagementTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private PortfolioService portfolioService;

    @Test
    public void testPortfolioCreation() throws Exception {
        mockMvc.perform(post("/portfolio/create")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    public void testPortfolioCalculation() throws Exception {
        mockMvc.perform(get("/portfolio/calculate")
                        .param("portfolioId", "101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalValue").exists());
    }
}