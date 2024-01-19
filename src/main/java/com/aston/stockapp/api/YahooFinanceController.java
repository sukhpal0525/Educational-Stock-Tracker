package com.aston.stockapp.api;

import com.aston.stockapp.domain.portfolio.PortfolioStock;
import com.aston.stockapp.domain.portfolio.PortfolioStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/stocks")
public class YahooFinanceController {

    private final YahooFinanceService yahooFinanceService;

    @Autowired
    public YahooFinanceController(YahooFinanceService yahooFinanceService) {
        this.yahooFinanceService = yahooFinanceService;
    }

    @GetMapping("/{symbol}")
    public String getStockData(@PathVariable String symbol, Model model) {
        YahooStock stock = yahooFinanceService.fetchStockData(symbol);
        model.addAttribute("stock", stock);
        return "stock";
    }

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
//            log.error("Error in search: ", e);
            // TODO: Handle exception appropriately
        }
        return "redirect:/stocks";
    }
}