package com.aston.stockapp.domain.portfolio.service;

import com.aston.stockapp.api.YahooFinanceService;
import com.aston.stockapp.api.YahooStock;
import com.aston.stockapp.domain.portfolio.*;
import com.aston.stockapp.user.CustomUserDetails;
import com.aston.stockapp.user.User;
import com.aston.stockapp.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Service
@Transactional
public class PortfolioService {

    @Autowired
    private PortfolioStockRepository portfolioStockRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private YahooFinanceService yahooFinanceService;

    @Autowired
    private UserRepository userRepository;

    public Portfolio getPortfolio() {
        Long currentUserId = getCurrentUser();
        Portfolio portfolio = portfolioRepository.findByUserId(currentUserId)
                .orElseGet(() -> createPortfolio(currentUserId));
        calculatePortfolioStats(portfolio);
        return portfolio;
    }

    private Portfolio createPortfolio(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Portfolio newPortfolio = new Portfolio();
        newPortfolio.setUser(user);
        return portfolioRepository.save(newPortfolio);
    }

    public void addStockToPortfolio(String symbol, int quantity) {
        // Check if stock exists in PortfolioStockRepository
        PortfolioStock stock = portfolioStockRepository.findByTicker(symbol)
                .orElseGet(() -> createPortfolioStock(symbol));

        YahooStock yahooStock = yahooFinanceService.fetchStockData(symbol);

        // Retrieve the current portfolio
        Portfolio portfolio = getPortfolio();

        // Find or create the portfolio item
        PortfolioItem portfolioItem = portfolio.getItems().stream()
                .filter(item -> item.getStock().getTicker().equals(symbol))
                .findFirst()
                .orElseGet(() -> {
                    PortfolioItem newItem = new PortfolioItem();
                    newItem.setStock(stock);
                    newItem.setPortfolio(portfolio);
                    portfolio.getItems().add(newItem);
                    return newItem;
                });
        // Update quantity and purchase price
        portfolioItem.setQuantity(portfolioItem.getQuantity() + quantity);
        portfolioItem.setPurchasePrice(yahooStock.getPrice().doubleValue());

        portfolioRepository.save(portfolio);
    }

    private PortfolioStock createPortfolioStock(String symbol) {
        // Fetch stock data from YahooFinanceService
        YahooStock yahooStock = yahooFinanceService.fetchStockData(symbol);

        // Create or update PortfolioStock
        PortfolioStock portfolioStock = new PortfolioStock();
        portfolioStock.setTicker(yahooStock.getTicker());
        portfolioStock.setName(yahooStock.getName());
        portfolioStock.setCurrentPrice(yahooStock.getPrice().intValue());

        return portfolioStockRepository.save(portfolioStock);
    }

    private void calculatePortfolioStats(Portfolio portfolio) {
        double totalCost = 0.0;
        double totalValue = 0.0;
        for (PortfolioItem item : portfolio.getItems()) {
            double cost = item.getPurchasePrice() * item.getQuantity();
            double value = item.getStock().getCurrentPrice() * item.getQuantity();
            totalCost += cost;
            totalValue += value;
        }
        double totalChangePercent = totalCost != 0 ? ((totalValue - totalCost) / totalCost) * 100 : 0;
        portfolio.setTotalCost(totalCost);
        portfolio.setTotalValue(totalValue);
        portfolio.setTotalChangePercent(totalChangePercent);
    }

    private Long getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUser().getId();
    }
}