package com.aston.stockapp.domain.portfolio;

import com.aston.stockapp.domain.portfolio.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    @GetMapping("/portfolio")
    public String viewPortfolio(Model model) {
        // Fetch portfolio for the logged in user and add to model
        Portfolio portfolio = portfolioService.getPortfolioForCurrentUser();
        model.addAttribute("portfolio", portfolio.getItems());
        return "portfolio";
    }

    @PostMapping("/portfolio/add")
    public String addToPortfolio(@RequestParam String symbol, @RequestParam int quantity) {
        // Add stock to user's portfolio
        portfolioService.addStockToPortfolio(symbol, quantity);
        return "redirect:/portfolio";
    }
}