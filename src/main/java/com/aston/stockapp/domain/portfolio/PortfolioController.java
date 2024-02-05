package com.aston.stockapp.domain.portfolio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    @Autowired PortfolioRepository portfolioRepository;

    @GetMapping("/portfolio")
    public String viewPortfolio(Model model) {
        // Fetch portfolio for the logged in user and add to model
        Portfolio portfolio = portfolioService.getPortfolio();
        model.addAttribute("portfolio", portfolio);
        model.addAttribute("totalCost", portfolio.getTotalCost());
        model.addAttribute("totalValue", portfolio.getTotalValue());
        model.addAttribute("totalChangePercent", portfolio.getTotalChangePercent());
        model.addAttribute("isEditing", false);
        return "portfolio";
    }

    @PostMapping("/portfolio/add")
    public String addToPortfolio(@RequestParam String symbol, @RequestParam int quantity) {
        // Add stock to user's portfolio
        portfolioService.addStockToPortfolio(symbol, quantity);
        return "redirect:/portfolio";
    }

    @GetMapping("/portfolio/edit")
    public String editPortfolio(Model model) {
        // Fetch portfolio for the logged in user and add to model
        Portfolio portfolio = portfolioService.getPortfolio();
        model.addAttribute("portfolio", portfolio);
        model.addAttribute("totalCost", portfolio.getTotalCost());
        model.addAttribute("totalValue", portfolio.getTotalValue());
        model.addAttribute("totalChangePercent", portfolio.getTotalChangePercent());
        model.addAttribute("isEditing", true);
        return "portfolio";
    }

    @PostMapping("/portfolio/save")
    public String savePortfolio(@ModelAttribute("portfolio") Portfolio portfolio) {
        // Save the updated portfolio items
        portfolioService.updatePortfolio(portfolio.getItems());
        return "redirect:/portfolio";
    }

    @PostMapping("/portfolio/update")
    public String updatePortfolioItem(@RequestParam("itemId") Long itemId, @RequestParam("quantity") int quantity) {
        portfolioService.updatePortfolioItem(itemId, quantity);
        return "redirect:/portfolio";
    }
}