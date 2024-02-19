package com.aston.stockapp.api;

import com.aston.stockapp.domain.portfolio.PortfolioService;
import com.aston.stockapp.domain.portfolio.PortfolioStock;
import com.aston.stockapp.domain.portfolio.PortfolioStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/stocks")
public class YahooFinanceController {

    @Autowired private PortfolioService portfolioService;
    private final YahooFinanceService yahooFinanceService;

    @Autowired
    public YahooFinanceController(YahooFinanceService yahooFinanceService) {
        this.yahooFinanceService = yahooFinanceService;
    }

    @GetMapping("/{symbol}")
    public String getStockData(@PathVariable String symbol, @RequestParam(defaultValue = "10y") String range, Model model) {
        YahooStock stock = yahooFinanceService.fetchStockData(symbol);
        String historicalDataJson = yahooFinanceService.fetchHistoricalData(symbol, range);

        model.addAttribute("stockData", stock);
        model.addAttribute("historicalDataJson", historicalDataJson);
        portfolioService.getCurrentUserBalance().ifPresent(balance -> model.addAttribute("balance", balance));
        return "stock";
    }

//    @GetMapping("/stocks/{symbol}/data")
//    @ResponseBody
//    public String fetchHistoricalData(@PathVariable String symbol, @RequestParam(required = false) String range) {
//        return yahooFinanceService.fetchHistoricalData(symbol, range);
//    }

    @GetMapping("/search")
    public String searchStock(@RequestParam String query) {
        String ticker = yahooFinanceService.getTickerFromName(query);
        if (ticker == null) {
            ticker = query;
        }
        try {
            YahooStock stock = yahooFinanceService.fetchStockData(ticker);
            if (stock != null) {
                return "redirect:/stocks/" + stock.getTicker();
            }
        } catch (Exception e) {
//            log.error("Error in searchStock: ", e);
            // TODO: Handle exception appropriately
        }
        return "redirect:/stocks";
    }

    @GetMapping("/valid-stock")
    public String validStock(Model model) {
        model.addAttribute("modalMessage", "Loading data for this stock.");
        return "index";
    }

    @GetMapping("/invalid-stock")
    public String invalidStock(Model model) {
        model.addAttribute("modalMessage", "This stock doesn't exist.");
        return "index";
    }

    @GetMapping("/no-query")
    public String noQuery(Model model) {
        model.addAttribute("modalMessage", "Please provide a query.");
        return "index";
    }
}