package com.aston.stockapp.domain.news;

import com.aston.stockapp.api.YahooFinanceService;
import com.aston.stockapp.domain.portfolio.Portfolio;
import com.aston.stockapp.domain.portfolio.PortfolioItem;
import com.aston.stockapp.domain.portfolio.PortfolioService;
import com.aston.stockapp.user.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/news")
public class StockNewsController {

    @Autowired private YahooFinanceService yahooFinanceService;
    @Autowired private PortfolioService portfolioService;

    @GetMapping("/new")
    public String getNews() {
        return "news";
    }

    @GetMapping("")
    public String getPortfolioNews(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getUser().getId();

            Portfolio portfolio = portfolioService.getPortfolio(userId);
            Map<String, List<StockNews>> newsByTicker = new HashMap<>();

            for (PortfolioItem item : portfolio.getItems()) {
                String ticker = item.getStock().getTicker();
                List<StockNews> news = yahooFinanceService.fetchNewsByTicker(ticker);
                newsByTicker.put(ticker, news);
            }

            model.addAttribute("newsByTicker", newsByTicker);
            model.addAttribute("portfolioItems", portfolio.getItems());
            return "news";
        }
        return "redirect:/login";
    }

}

//    @GetMapping("/n")
//    @ResponseBody
//    public String fetchHistoricalData(@PathVariable String symbol, @RequestParam(required = false) String range) {
//        return String.valueOf(yahooFinanceService.fetchStockNews());
//    }
