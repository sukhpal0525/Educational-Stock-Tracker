package com.aston.stockapp.api;

import com.aston.stockapp.domain.portfolio.PortfolioService;
import com.aston.stockapp.domain.transaction.TransactionRepository;
import com.aston.stockapp.user.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/stocks")
public class YahooFinanceController {

    @Autowired private PortfolioService portfolioService;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private YahooFinanceService yahooFinanceService;

    @Autowired
    public YahooFinanceController(YahooFinanceService yahooFinanceService) {
        this.yahooFinanceService = yahooFinanceService;
    }

    @GetMapping("/{symbol}")
    public String getStockData(@PathVariable String symbol, @RequestParam(defaultValue = "10y") String range, @RequestParam(required = false) Integer quantity, Authentication authentication, Model model) {
        // Fetch and wait for all stock data asynchronously
        CompletableFuture<YahooStock> stockFuture = yahooFinanceService.fetchStockDataAsync(symbol);
        CompletableFuture<String> historicalDataFuture = yahooFinanceService.fetchHistoricalDataAsync(symbol, range);
        CompletableFuture<YahooStock> stockInfoFuture = yahooFinanceService.fetchStockInfoAsync(symbol);
        CompletableFuture.allOf(stockFuture, historicalDataFuture, stockInfoFuture).join();

        // Populate model with stock data
        model.addAttribute("stockData", stockFuture.join());
        model.addAttribute("historicalDataJson", historicalDataFuture.join());
        model.addAttribute("stockInfo", stockInfoFuture.join());

        // Handle sell action if its present
        if (quantity != null && authentication != null && authentication.isAuthenticated()) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getUser().getId();
            try {
                portfolioService.updateStockInPortfolio(userId, symbol, quantity, false); // False = selling
                model.addAttribute("transactionSuccess", "Successfully sold " + quantity + " units of " + symbol + ".");
            } catch (IllegalArgumentException e) {
                // Set the model attribute for the error message
                model.addAttribute("transactionFailed", e.getMessage());
            }
        }

        // Check if the user is authenticated to populate user-related data
        if (authentication != null && authentication.isAuthenticated()) {
            Long userId = ((CustomUserDetails) authentication.getPrincipal()).getUser().getId();
            model.addAttribute("ownsStock", portfolioService.ownsStock(symbol));
            model.addAttribute("transactions", transactionRepository.findByUserIdAndStockTicker(userId, symbol));
            model.addAttribute("quantity", portfolioService.getPortfolio(userId).getItems());
            portfolioService.getCurrentUserBalance().ifPresent(balance -> model.addAttribute("balance", balance));
        }
        return "stock";
    }


//    @GetMapping("/stocks/{symbol}/data")
//    @ResponseBody
//    public String fetchHistoricalData(@PathVariable String symbol, @RequestParam(required = false) String range) {
//        return yahooFinanceService.fetchHistoricalDataAsync(symbol, range);
//    }

    @GetMapping("/search")
    public CompletableFuture<String> searchStock(@RequestParam String query) {
        String ticker = yahooFinanceService.getTickerFromName(query);
        if (ticker == null) {
            ticker = query;
        }
        return yahooFinanceService.fetchStockDataAsync(ticker).thenApply(stock -> {
            if (stock != null) {
                return "redirect:/stocks/" + stock.getTicker();
            } else {
                return "redirect:/stocks";
            }
        }).exceptionally(ex -> {
            // log.error("Error in searchStock: ", ex);
            // TODO: Handle exception appropriately
            return "redirect:/stocks";
        });
    }
}