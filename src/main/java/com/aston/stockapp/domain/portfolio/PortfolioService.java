package com.aston.stockapp.domain.portfolio;

import com.aston.stockapp.api.YahooFinanceService;
import com.aston.stockapp.api.YahooStock;
import com.aston.stockapp.domain.transaction.Transaction;
import com.aston.stockapp.domain.transaction.TransactionRepository;
import com.aston.stockapp.user.CustomUserDetails;
import com.aston.stockapp.user.User;
import com.aston.stockapp.user.repository.UserRepository;
import com.aston.stockapp.util.InsufficientBalanceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;

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

    public void addStockToPortfolio(String symbol, int quantity, BigDecimal finalPrice, boolean isBuying) {
        Long currentUserId = getCurrentUser();
        User user = userRepository.findById(currentUserId).orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + currentUserId));
        BigDecimal cost = finalPrice.multiply(new BigDecimal(quantity));

        if (isBuying && user.getBalance().compareTo(cost) < 0) {
            BigDecimal missingMoney = cost.subtract(user.getBalance());
            DecimalFormat moneyFormat = new DecimalFormat("#,##0.00");
            String formattedMissingMoney = moneyFormat.format(missingMoney);
            throw new InsufficientBalanceException("Insufficient balance. You needed $" + formattedMissingMoney + " more to complete this purchase.");
        }

        Portfolio portfolio = getPortfolio(currentUserId);
        Optional<PortfolioStock> existingStockOpt = portfolioStockRepository.findByTicker(symbol);

        PortfolioItem portfolioItem = portfolio.getItems().stream()
                .filter(item -> item.getStock().getTicker().equals(symbol))
                .findFirst()
                .orElseGet(() -> {
                    PortfolioStock stock = portfolioStockRepository.findByTicker(symbol).orElseGet(() -> createPortfolioStock(symbol)); // Create and fetch new stock with volatility data
                    PortfolioItem newItem = new PortfolioItem();
                    newItem.setStock(stock);
                    newItem.setQuantity(0);
                    newItem.setPortfolio(portfolio);
                    portfolio.getItems().add(newItem);
                    return newItem;
                });

        if (isBuying) {
            portfolioItem.setQuantity(portfolioItem.getQuantity() + quantity);
            // Convert BigDecimal to double for setting purchase price
            portfolioItem.setPurchasePrice(finalPrice.doubleValue());
            user.setBalance(user.getBalance().subtract(cost));
        } else {
            // Check if the user does not own the stock at all
            if (portfolioItem.getQuantity() == 0) {
                throw new IllegalArgumentException("Cannot sell stock not owned.");
            }
            // Check if the user is trying to sell more than they own
            if (portfolioItem.getQuantity() < Math.abs(quantity)) {
                throw new IllegalArgumentException("Cannot sell more than owned.");
            }
            portfolioItem.setQuantity(portfolioItem.getQuantity() - Math.abs(quantity));
            BigDecimal saleProceeds = finalPrice.multiply(new BigDecimal(Math.abs(quantity)));
            user.setBalance(user.getBalance().add(saleProceeds));
        }

        if (portfolioItem.getQuantity() <= 0) {
            portfolio.getItems().remove(portfolioItem);
            portfolioItemRepository.delete(portfolioItem);
        } else {
            portfolioItemRepository.save(portfolioItem);
        }

        userRepository.save(user);

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setDateTime(LocalDateTime.now());
        transaction.setStockTicker(symbol);
        transaction.setQuantity(Math.abs(quantity));
        transaction.setPurchasePrice(finalPrice);
        transaction.setTotalCost(cost.abs());
        transaction.setTransactionType(isBuying ? "Buy" : "Sell");
        transactionRepository.save(transaction);
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

//    public PortfolioStock createPortfolioStock(String symbol) {
//        YahooStock yahooStockData = yahooFinanceService.fetchStockData(symbol);
//        YahooStock yahooStockInfo = yahooFinanceService.fetchStockInfo(symbol);
//
//        PortfolioStock portfolioStock = new PortfolioStock();
//        portfolioStock.setTicker(yahooStockData.getTicker());
//        portfolioStock.setName(yahooStockData.getName());
//        portfolioStock.setCurrentPrice(yahooStockData.getPrice().doubleValue());
//
//        portfolioStock.setSector(yahooStockInfo.getSector());
//        portfolioStock.setFiftyTwoWeekHigh(BigDecimal.valueOf(yahooStockData.getFiftyTwoWeekHigh()));
//        portfolioStock.setFiftyTwoWeekLow(BigDecimal.valueOf(yahooStockData.getFiftyTwoWeekLow()));
//        portfolioStock.setRegularMarketChangePercent(yahooStockData.getRegularMarketChangePercent());
//
//        PortfolioStock savedStock = portfolioStockRepository.save(portfolioStock);
//        System.out.println("Saved stock: " + savedStock.getName() + " with sector: " + savedStock.getSector());
//        return savedStock;
//    }

    public PortfolioStock createPortfolioStock(String symbol) {
        YahooStock yahooStockData = yahooFinanceService.fetchStockData(symbol);
        YahooStock yahooStockInfo = yahooFinanceService.fetchStockInfo(symbol);

        PortfolioStock portfolioStock = new PortfolioStock();

        // Set all fields
        portfolioStock.setTicker(yahooStockData.getTicker());
        portfolioStock.setName(yahooStockData.getName());
        portfolioStock.setCurrentPrice(yahooStockData.getPrice().doubleValue());
        portfolioStock.setSector(yahooStockInfo.getSector());
        portfolioStock.setFiftyTwoWeekHigh(BigDecimal.valueOf(yahooStockData.getFiftyTwoWeekHigh()));
        portfolioStock.setFiftyTwoWeekLow(BigDecimal.valueOf(yahooStockData.getFiftyTwoWeekLow()));
        portfolioStock.setRegularMarketChangePercent(yahooStockData.getRegularMarketChangePercent());

        PortfolioStock savedStock = portfolioStockRepository.save(portfolioStock);
        System.out.println("Saved stock: " + savedStock.getName() + " with sector: " + savedStock.getSector() + ", 52 Week High: " + savedStock.getFiftyTwoWeekHigh() + ", 52 Week Low: " + savedStock.getFiftyTwoWeekLow() + ", Regular Market Change %: " + savedStock.getRegularMarketChangePercent());
        return savedStock;
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
        double totalChangeValue = totalValue - totalCost;
        double totalChangePercent = totalCost > 0 ? (totalChangeValue / totalCost * 100) : 0;

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

    @Transactional
    public void deletePortfolioItem(Long itemId) {
        PortfolioItem item = portfolioItemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Portfolio item not found with ID: " + itemId));

        User user = item.getPortfolio().getUser();
        Portfolio portfolio = item.getPortfolio();

        // Calculate sale proceeds as if selling the stock
        BigDecimal saleProceeds = BigDecimal.valueOf(item.getQuantity()).multiply(BigDecimal.valueOf(item.getStock().getCurrentPrice()));

        user.setBalance(user.getBalance().add(saleProceeds.abs()));
        userRepository.save(user);

        // Log the "sale" of the portfolio item, even though its being deleted.
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setDateTime(LocalDateTime.now());
        transaction.setStockTicker(item.getStock().getTicker());
        transaction.setQuantity(item.getQuantity());
        transaction.setPurchasePrice(BigDecimal.valueOf(item.getStock().getCurrentPrice()));
        transaction.setTotalCost(saleProceeds);
        transaction.setTransactionType("Sell (Deleted)");
        transactionRepository.save(transaction);

        portfolio.getItems().remove(item);
        portfolioItemRepository.delete(item);

        calculatePortfolioStats(portfolio);
    }

    public void updateStockInPortfolio(Long userId, String symbol, int quantity, boolean isBuying) {
        if (userId == null) {
            throw new IllegalStateException("No user ID provided");
        }

        Portfolio portfolio = portfolioRepository.findByUserId(userId).orElseGet(() -> createPortfolio(userId));
        Optional<PortfolioItem> existingItemOptional = portfolio.getItems().stream().filter(item -> item.getStock().getTicker().equals(symbol)).findFirst();

        if (isBuying) {
            PortfolioItem portfolioItem = existingItemOptional.orElseGet(() -> {
                PortfolioStock stock = createPortfolioStock(symbol);
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
                    throw new IllegalArgumentException("Attempted to sell more units than owned (" + portfolioItem.getQuantity() + " available)");
                }
                int newQuantity = portfolioItem.getQuantity() - quantity;
                portfolioItem.setQuantity(newQuantity);
                if (newQuantity == 0) {
                    portfolio.getItems().remove(portfolioItem);
                    portfolioItemRepository.delete(portfolioItem);
                }
            } else {
                throw new IllegalArgumentException("Attempted to sell stock not owned");
            }
        }

        portfolioRepository.save(portfolio);

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

    public Map<String, BigDecimal> getPortfolioSectorDistribution(Long userId) {
        Portfolio portfolio = getPortfolio(userId);
        BigDecimal totalValue = BigDecimal.ZERO;
        Map<String, BigDecimal> sectorValues = new HashMap<>();

        for (PortfolioItem item : portfolio.getItems()) {
            BigDecimal itemValue = BigDecimal.valueOf(item.getQuantity()).multiply(BigDecimal.valueOf(item.getStock().getCurrentPrice()));
            totalValue = totalValue.add(itemValue);
            sectorValues.merge(item.getStock().getSector(), itemValue, BigDecimal::add);
        }

        // Convert sector values to percentages of the total value
        for (Map.Entry<String, BigDecimal> entry : sectorValues.entrySet()) {
            BigDecimal percentage = entry.getValue().multiply(new BigDecimal(100)).divide(totalValue, 2, RoundingMode.HALF_UP);
            sectorValues.put(entry.getKey(), percentage);
        }
        return sectorValues;
    }

    public BigDecimal calculatePortfolioVolatility(Long userId) {
        Portfolio portfolio = getPortfolio(userId);
        BigDecimal portfolioVolatility = BigDecimal.ZERO;

        if (portfolio.getItems().isEmpty()) {
            return BigDecimal.ZERO;
        }

        // Calculate the total quantity of each stock ticker in the portfolio
        Map<String, Integer> tickerQuantityMap = new HashMap<>();
        for (PortfolioItem item : portfolio.getItems()) {
            tickerQuantityMap.merge(item.getStock().getTicker(), item.getQuantity(), Integer::sum);
        }

        BigDecimal totalWeightedVolatility = BigDecimal.ZERO;
        BigDecimal totalQuantity = BigDecimal.ZERO;

        for (PortfolioItem item : portfolio.getItems()) {
            BigDecimal stockVolatility = calculateStockVolatility(item.getStock());
            BigDecimal itemQuantity = BigDecimal.valueOf(item.getQuantity());

            // Calculate the concentration factor for this stock based on its quantity relative to the total quantity of this ticker
            BigDecimal concentrationFactor = new BigDecimal(tickerQuantityMap.get(item.getStock().getTicker()));
            concentrationFactor = concentrationFactor.multiply(new BigDecimal("0.1"));

            // Adjust stock volatility based on its concentration in the portfolio
            stockVolatility = stockVolatility.multiply(concentrationFactor);

            totalWeightedVolatility = totalWeightedVolatility.add(stockVolatility.multiply(itemQuantity));
            totalQuantity = totalQuantity.add(itemQuantity);
        }

        // Avoid division by zero
        if (totalQuantity.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        // Calculate the average portfolio volatility, weighted by the quantity of each stock
        portfolioVolatility = totalWeightedVolatility.divide(totalQuantity, 2, RoundingMode.HALF_UP);

        return portfolioVolatility;
    }

    public BigDecimal calculateStockVolatility(PortfolioStock stock) {
        BigDecimal fiftyTwoWeekHigh = Optional.ofNullable(stock.getFiftyTwoWeekHigh()).orElse(BigDecimal.ZERO);
        BigDecimal fiftyTwoWeekLow = Optional.ofNullable(stock.getFiftyTwoWeekLow()).orElse(BigDecimal.ZERO);
        BigDecimal dailyVolatility = Optional.ofNullable(stock.getRegularMarketChangePercent()).orElse(BigDecimal.ZERO);

        if (fiftyTwoWeekHigh.compareTo(BigDecimal.ZERO) == 0 || fiftyTwoWeekLow.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal fiftyTwoWeekRange = fiftyTwoWeekHigh.subtract(fiftyTwoWeekLow);
        BigDecimal priceRangeVolatility = fiftyTwoWeekRange.divide(BigDecimal.valueOf(stock.getCurrentPrice()), RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        BigDecimal averageVolatility = dailyVolatility.add(priceRangeVolatility).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);

        System.out.println("Calculating volatility for stock: " + stock.getTicker());
        System.out.println("Daily Volatility: " + dailyVolatility);
        System.out.println("52 Week High: " + stock.getFiftyTwoWeekHigh());
        System.out.println("52 Week Low: " + stock.getFiftyTwoWeekLow());
        System.out.println("Price Range Volatility: " + priceRangeVolatility);
        System.out.println("Average Volatility: " + averageVolatility);

        return averageVolatility;
    }

//    public BigDecimal calculatePortfolioVolatility(Long userId) {
//        Portfolio portfolio = getPortfolio(userId);
//        BigDecimal portfolioVolatility = BigDecimal.ZERO;
//
//        // Check if the portfolio has items to avoid division by zero
//        if (!portfolio.getItems().isEmpty()) {
//            for (PortfolioItem item : portfolio.getItems()) {
//                YahooStock stockData = yahooFinanceService.fetchStockData(item.getStock().getTicker());
//                BigDecimal stockVolatility = calculateStockVolatility(stockData);
//                portfolioVolatility = portfolioVolatility.add(stockVolatility.multiply(BigDecimal.valueOf(item.getQuantity())));
//            }
//            portfolioVolatility = portfolioVolatility.divide(BigDecimal.valueOf(portfolio.getItems().size()), 2, RoundingMode.HALF_UP);
//        } else {
//            portfolioVolatility = BigDecimal.ZERO;
//        }
//        return portfolioVolatility;
//    }
//
//    public BigDecimal calculateStockVolatility(YahooStock stockData) {
//        BigDecimal dailyVolatility = stockData.getRegularMarketChangePercent();
//
//        // Calculate the range of price change over the last 52 weeks as a simple volatility measure
//        BigDecimal fiftyTwoWeekRange = BigDecimal.valueOf(stockData.getFiftyTwoWeekHigh()).subtract(BigDecimal.valueOf(stockData.getFiftyTwoWeekLow()));
//        BigDecimal priceRangeVolatility = fiftyTwoWeekRange.divide(stockData.getPrice(), RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
//
//        // Average the daily volatility and the 52 week range volatility for basic volatility
//        BigDecimal averageVolatility = dailyVolatility.add(priceRangeVolatility).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
//
//        System.out.println("Calculating volatility for stock: " + stockData.getTicker());
//        System.out.println("Daily Volatility: " + dailyVolatility);
//        System.out.println("52 Week High: " + stockData.getFiftyTwoWeekHigh());
//        System.out.println("52 Week Low: " + stockData.getFiftyTwoWeekLow());
//        System.out.println("Price Range Volatility: " + priceRangeVolatility);
//        System.out.println("Average Volatility: " + averageVolatility);
//
//        return averageVolatility;
//    }








//    public List<BigDecimal> calculateHistoricalPerformance(List<PortfolioItem> portfolioItems) {
//        List<BigDecimal> performance = new ArrayList<>();
//        for (PortfolioItem item : portfolioItems) {
//            BigDecimal purchaseValue = BigDecimal.valueOf(item.getPurchasePrice() * item.getQuantity());
//            BigDecimal currentValue = BigDecimal.valueOf(item.getStock().getCurrentPrice() * item.getQuantity());
//            BigDecimal performancePercentage = currentValue.subtract(purchaseValue)
//                    .divide(purchaseValue, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100));
//            performance.add(performancePercentage);
//        }
//        System.out.println("Historical Performance: " + performance);
//        return performance;
//    }

    public List<BigDecimal> calculateHistoricalPerformance(List<PortfolioItem> portfolioItems) {
        List<BigDecimal> performance = new ArrayList<>();
        int numberOfPeriods = 12;
        for (int i = 0; i < numberOfPeriods; i++) {
            BigDecimal periodPerformance = calculatePeriodPerformance(portfolioItems, i);
            performance.add(periodPerformance);
        }
        // Ensures the performance list matches the expected number of periods
        while (performance.size() < numberOfPeriods) {
            performance.add(BigDecimal.ZERO);
        }
        System.out.println("Historical Performance: " + performance);
        return performance;
    }

    private BigDecimal calculatePeriodPerformance(List<PortfolioItem> portfolioItems, int periodIndex) {
        if (portfolioItems.isEmpty()) {
            return BigDecimal.ZERO;
        }

        Map<String, Integer> tickerFrequency = new HashMap<>();
        for (PortfolioItem item : portfolioItems) {
            tickerFrequency.merge(item.getStock().getTicker(), item.getQuantity(), Integer::sum);
        }

        BigDecimal totalPerformance = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        for (PortfolioItem item : portfolioItems) {
            BigDecimal weight = BigDecimal.valueOf(item.getQuantity());
            BigDecimal basePerformance = item.getStock().getRegularMarketChangePercent();
            // Calculate the concentration factor based on the quantity of each stock and its frequency in the portfolio
            BigDecimal concentrationFactor = new BigDecimal(tickerFrequency.get(item.getStock().getTicker())).multiply(new BigDecimal("0.05"));
            BigDecimal adjustedPerformance = basePerformance.add(concentrationFactor).multiply(new BigDecimal(periodIndex + 1).multiply(new BigDecimal("0.1")));
            BigDecimal performance = adjustedPerformance.multiply(weight);

            totalPerformance = totalPerformance.add(performance);
            totalWeight = totalWeight.add(weight);
        }

        if (totalWeight.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal averagePerformance = totalPerformance.divide(totalWeight, 4, RoundingMode.HALF_UP);
        return averagePerformance;
    }

    public BigDecimal forecastFutureValue(List<PortfolioItem> portfolioItems, int monthsAhead) {
        if (portfolioItems.isEmpty()) {
            System.out.println("No portfolio items to forecast future value.");
            return BigDecimal.ZERO;
        }

        BigDecimal averageMonthlyGrowthRate = BigDecimal.ZERO;
        for (PortfolioItem item : portfolioItems) {
            averageMonthlyGrowthRate = averageMonthlyGrowthRate.add(item.getStock().getRegularMarketChangePercent());
        }
        if (!portfolioItems.isEmpty()) {
            averageMonthlyGrowthRate = averageMonthlyGrowthRate.divide(BigDecimal.valueOf(portfolioItems.size()), 4, BigDecimal.ROUND_HALF_UP);
        }

        BigDecimal conservativeFactor = new BigDecimal("0.5");
        averageMonthlyGrowthRate = averageMonthlyGrowthRate.multiply(conservativeFactor);

        BigDecimal forecastedValue = BigDecimal.ZERO;
        for (PortfolioItem item : portfolioItems) {
            BigDecimal currentValue = BigDecimal.valueOf(item.getStock().getCurrentPrice() * item.getQuantity());
            BigDecimal futureValue = currentValue.multiply(averageMonthlyGrowthRate.divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP).add(BigDecimal.ONE).pow(monthsAhead));
            forecastedValue = forecastedValue.add(futureValue);
        }
        System.out.println("Forecasted Future Value: " + forecastedValue);
        return forecastedValue;
    }

    public Page<Portfolio> getAllPortfolios(Pageable pageable) {
        return portfolioRepository.findAll(pageable);
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