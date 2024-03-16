package com.aston.stockapp.domain.portfolio;

import com.aston.stockapp.api.YahooFinanceService;
import com.aston.stockapp.domain.transaction.Transaction;
import com.aston.stockapp.domain.transaction.TransactionRepository;
import com.aston.stockapp.user.CustomUserDetails;
import com.aston.stockapp.user.User;
import com.aston.stockapp.user.repository.UserRepository;
import com.aston.stockapp.util.InsufficientBalanceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class PortfolioService {

    @Autowired private PortfolioStockRepository portfolioStockRepository;
    @Autowired private PortfolioItemRepository portfolioItemRepository;
    @Autowired private PortfolioRepository portfolioRepository;
    @Autowired private YahooFinanceService yahooFinanceService;
    @Autowired private UserRepository userRepository;
    @Autowired private TransactionRepository transactionRepository;

    public Portfolio getPortfolio(Long userId) {
        Portfolio portfolio = portfolioRepository.findByUserId(userId).orElseGet(() -> createPortfolio(userId));
        calculatePortfolioStats(portfolio);
        return portfolio;
    }

    private Portfolio createPortfolio(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Portfolio newPortfolio = new Portfolio();
        newPortfolio.setUser(user);
        return portfolioRepository.save(newPortfolio);
    }

    public CompletableFuture<Void> addStockToPortfolio(String symbol, int quantity, BigDecimal finalPrice, boolean isBuying) {
        Long currentUserId = getCurrentUser();
        User user = userRepository.findById(currentUserId).orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + currentUserId));
        BigDecimal cost = finalPrice.multiply(BigDecimal.valueOf(quantity));
        DecimalFormat moneyFormat = new DecimalFormat("#,##0.00");

        if (isBuying && user.getBalance().compareTo(cost) < 0) {
            BigDecimal missingMoney = cost.subtract(user.getBalance());
            String formattedMissingMoney = moneyFormat.format(missingMoney);
            throw new InsufficientBalanceException("Insufficient balance. You needed $" + formattedMissingMoney + " more to complete this purchase.");
        }

        return CompletableFuture.runAsync(() -> {
            Portfolio portfolio = getPortfolio(currentUserId);
            PortfolioItem portfolioItem = portfolio.getItems().stream().filter(item -> item.getStock().getTicker().equals(symbol)).findFirst().orElseGet(() -> {
                PortfolioStock stock = createPortfolioStock(symbol).join();
                PortfolioItem newItem = new PortfolioItem();
                newItem.setStock(stock);
                newItem.setPortfolio(portfolio);
                portfolio.getItems().add(newItem);
                return newItem;
            });

            BigDecimal newBalance = isBuying ? user.getBalance().subtract(cost) : user.getBalance().add(cost);
            user.setBalance(newBalance);
            userRepository.save(user);

            // Adjust quantity based on buying or selling
            int newQuantity = portfolioItem.getQuantity() + (isBuying ? quantity : -quantity);
            if (newQuantity <= 0) {
                portfolio.getItems().remove(portfolioItem);
                portfolioItemRepository.delete(portfolioItem);
            } else {
                portfolioItem.setQuantity(newQuantity);
                if (isBuying) {
                    portfolioItem.setPurchasePrice(finalPrice.doubleValue());
                }
                portfolioRepository.save(portfolio);
            }

            // Record the transaction
            Transaction transaction = new Transaction();
            transaction.setUser(user);
            transaction.setDateTime(LocalDateTime.now());
            transaction.setStockTicker(symbol);
            transaction.setQuantity(quantity);
            transaction.setPurchasePrice(finalPrice);
            transaction.setTotalCost(cost);
            transaction.setTransactionType(isBuying ? "Buy" : "Sell");
            transactionRepository.save(transaction);
        });
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

    private CompletableFuture<PortfolioStock> createPortfolioStock(String symbol) {
        return yahooFinanceService.fetchStockDataAsync(symbol).thenApply(yahooStock -> {
            PortfolioStock portfolioStock = new PortfolioStock();
            portfolioStock.setTicker(yahooStock.getTicker());
            portfolioStock.setName(yahooStock.getName());
            portfolioStock.setCurrentPrice(yahooStock.getPrice().intValue());

            return portfolioStockRepository.save(portfolioStock);
        });
    }

    public void calculatePortfolioStats(Portfolio portfolio) {
        double totalCost = 0.0;
        double totalValue = 0.0;
        for (PortfolioItem item : portfolio.getItems()) {
            double cost = item.getPurchasePrice() * item.getQuantity();
            double value = item.getStock().getCurrentPrice() * item.getQuantity();
            totalCost += cost;
            totalValue += value;
        }
        double totalChangePercent = totalCost > 0 ? (totalValue - totalCost) / totalCost * 100 : 0;
        portfolio.setTotalCost(totalCost);
        portfolio.setTotalValue(totalValue);
        portfolio.setTotalChangePercent(totalChangePercent);

        portfolioRepository.save(portfolio);
    }

    public boolean ownsStock(String tickerSymbol) {
        Long currentUserId = getCurrentUser();
        if (currentUserId == null) {
            return false;
        }
        Portfolio portfolio = getPortfolio(currentUserId);
        return portfolio.getItems().stream().anyMatch(item -> item.getStock().getTicker().equals(tickerSymbol) && item.getQuantity() > 0);
    }

    public void updateStockInPortfolio(Long userId, String symbol, int quantity, boolean isBuying) {
        if (userId == null) {
            throw new IllegalStateException("No user ID provided");
        }

        Portfolio portfolio = portfolioRepository.findByUserId(userId).orElseGet(() -> createPortfolio(userId));
        Optional<PortfolioItem> existingItemOptional = portfolio.getItems().stream()
                .filter(item -> item.getStock().getTicker().equals(symbol))
                .findFirst();

        if (isBuying) {
            PortfolioItem portfolioItem = existingItemOptional.orElseGet(() -> {
                CompletableFuture<PortfolioStock> stockFuture = createPortfolioStock(symbol);
                PortfolioStock stock = stockFuture.join();
                PortfolioItem newItem = new PortfolioItem();
                newItem.setStock(stock);
                newItem.setPortfolio(portfolio);
                portfolio.getItems().add(newItem);
                return newItem;
            });
            portfolioItem.setQuantity(portfolioItem.getQuantity() + quantity);
        } else {
            if (existingItemOptional.isPresent()) {
                PortfolioItem portfolioItem = existingItemOptional.get();
                if (portfolioItem.getQuantity() < quantity) {
                    // User doesn't have enough stock to sell
                    throw new IllegalArgumentException("Attempted to sell more units than owned (" + portfolioItem.getQuantity() + " available)");
                }
                int newQuantity = portfolioItem.getQuantity() - quantity;
                portfolioItem.setQuantity(newQuantity);
                if (newQuantity == 0) {
                    // Remove the item if the quantity becomes 0
                    portfolio.getItems().remove(portfolioItem);
                    portfolioItemRepository.delete(portfolioItem);
                }
            } else {
                // User doesn't own any of the stock they want to sell
                throw new IllegalArgumentException("Attempted to sell stock not owned");
            }
        }
        // Save changes to the portfolio and its items
        portfolioRepository.save(portfolio);

        // Update the user's balance and record the transaction
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        BigDecimal pricePerUnit = existingItemOptional.map(item -> BigDecimal.valueOf(item.getStock().getCurrentPrice())).orElse(BigDecimal.ZERO);
        BigDecimal transactionCost = pricePerUnit.multiply(BigDecimal.valueOf(quantity));

        if (isBuying) {
            user.setBalance(user.getBalance().subtract(transactionCost));
        } else {
            user.setBalance(user.getBalance().add(transactionCost));
        }
        userRepository.save(user);

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setDateTime(LocalDateTime.now());
        transaction.setStockTicker(symbol);
        transaction.setQuantity(quantity);
        transaction.setPurchasePrice(pricePerUnit);
        transaction.setTotalCost(isBuying ? transactionCost : transactionCost.negate());
        transactionRepository.save(transaction);
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

    public Long getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }
        // Check if the principal is an instance of CustomUserDetails
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUser().getId();
        } else {
            // TODO: Handle other cases like an anonymous user or different principal type
            throw new IllegalStateException("Authenticated user is not of expected type");
        }
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