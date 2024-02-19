package com.aston.stockapp.domain.portfolio;

import com.aston.stockapp.api.YahooFinanceService;
import com.aston.stockapp.api.YahooStock;
import com.aston.stockapp.domain.portfolio.*;
import com.aston.stockapp.domain.transaction.Transaction;
import com.aston.stockapp.domain.transaction.TransactionRepository;
import com.aston.stockapp.user.CustomUserDetails;
import com.aston.stockapp.user.User;
import com.aston.stockapp.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PortfolioService {

    @Autowired private PortfolioStockRepository portfolioStockRepository;
    @Autowired private PortfolioItemRepository portfolioItemRepository;
    @Autowired private PortfolioRepository portfolioRepository;
    @Autowired private YahooFinanceService yahooFinanceService;
    @Autowired private UserRepository userRepository;
    @Autowired private TransactionRepository transactionRepository;

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
        PortfolioStock stock = portfolioStockRepository.findByTicker(symbol).orElseGet(() -> createPortfolioStock(symbol));

        YahooStock yahooStock = yahooFinanceService.fetchStockData(symbol);
        Portfolio portfolio = getPortfolio();
        PortfolioItem portfolioItem = portfolio.getItems().stream().filter(item -> item.getStock().getTicker().equals(symbol)).findFirst().orElseGet(() -> {PortfolioItem newItem = new PortfolioItem();
            newItem.setStock(stock);
            newItem.setPortfolio(portfolio);
            portfolio.getItems().add(newItem);
            return newItem;
        });

        double cost = yahooStock.getPrice().doubleValue() * quantity;
        User user = portfolio.getUser();
        BigDecimal newBalance = user.getBalance().subtract(BigDecimal.valueOf(cost));
        user.setBalance(newBalance);
        userRepository.save(user);

        portfolioItem.setQuantity(portfolioItem.getQuantity() + quantity);
        portfolioItem.setPurchasePrice(yahooStock.getPrice().doubleValue());

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setDateTime(LocalDateTime.now());
        transaction.setStockTicker(symbol);
        transaction.setQuantity(quantity);
        transaction.setPurchasePrice(yahooStock.getPrice());
        transaction.setTotalCost(BigDecimal.valueOf(cost));
        transactionRepository.save(transaction);
        portfolioRepository.save(portfolio);
    }

//    public void addStockToPortfolio(String symbol, int quantity) {
//        // Check if stock exists in PortfolioStockRepository
//        PortfolioStock stock = portfolioStockRepository.findByTicker(symbol)
//                .orElseGet(() -> createPortfolioStock(symbol));
//
//        YahooStock yahooStock = yahooFinanceService.fetchStockData(symbol);
//
//        // Retrieve the current portfolio
//        Portfolio portfolio = getPortfolio();
//
//        // Find or create the portfolio item
//        PortfolioItem portfolioItem = portfolio.getItems().stream()
//                .filter(item -> item.getStock().getTicker().equals(symbol))
//                .findFirst()
//                .orElseGet(() -> {
//                    PortfolioItem newItem = new PortfolioItem();
//                    newItem.setStock(stock);
//                    newItem.setPortfolio(portfolio);
//                    portfolio.getItems().add(newItem);
//                    return newItem;
//                });
//        // Update quantity and purchase price
//        portfolioItem.setQuantity(portfolioItem.getQuantity() + quantity);
//        portfolioItem.setPurchasePrice(yahooStock.getPrice().doubleValue());
//
//        portfolioRepository.save(portfolio);
//    }

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

    public void updatePortfolio(List<PortfolioItem> items) {
        for (PortfolioItem updatedItem : items) {
            PortfolioItem existingItem = portfolioItemRepository.findById(updatedItem.getId()).orElse(null);
            if (existingItem != null) {
                existingItem.setQuantity(updatedItem.getQuantity());
                portfolioItemRepository.save(existingItem);
            }
        }
        // Recalculate stats and update the portfolio
        Portfolio portfolio = getPortfolio();
        calculatePortfolioStats(portfolio);
        portfolioRepository.save(portfolio);
    }

//    // For editing the quantity of a user's item in their portfolio
//    public void updatePortfolioItem(Long itemId, int quantity) {
//        PortfolioItem item = portfolioItemRepository.findById(itemId)
//                .orElseThrow(() -> new EntityNotFoundException("Portfolio item not found"));
//
//        item.setQuantity(quantity);
//        portfolioItemRepository.save(item);
//
//        // Recalculate and update the portfolio stats
//        Portfolio portfolio = item.getPortfolio();
//        calculatePortfolioStats(portfolio);
//        portfolioRepository.save(portfolio);
//    }

    public void updatePortfolioItem(Long itemId, int newQuantity) {
        PortfolioItem item = portfolioItemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Portfolio item not found"));

        // Calculate the difference in stock quantity and the corresponding balance adjustment
        int quantityDifference = newQuantity - item.getQuantity();
        BigDecimal costDifference = BigDecimal.valueOf(item.getPurchasePrice()).multiply(BigDecimal.valueOf(quantityDifference));

        // Retrieve the user and adjust balance
        User user = item.getPortfolio().getUser();
        user.setBalance(user.getBalance().subtract(costDifference));
        userRepository.save(user);

        // Update the portfolio item with the new quantity
        item.setQuantity(newQuantity);
        portfolioItemRepository.save(item);

        Transaction transaction = new Transaction();
        Portfolio portfolio = item.getPortfolio();
        calculatePortfolioStats(portfolio);
        portfolioRepository.save(portfolio);
        transactionRepository.save(transaction);
    }

    private Long getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUser().getId();
    }

    public Optional<BigDecimal> getCurrentUserBalance() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getUser().getId();
            User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
            return Optional.of(user.getBalance());
        }
        return Optional.empty();
    }
}