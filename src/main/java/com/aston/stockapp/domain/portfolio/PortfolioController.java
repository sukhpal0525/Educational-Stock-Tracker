package com.aston.stockapp.domain.portfolio;

import com.aston.stockapp.api.YahooFinanceService;
import com.aston.stockapp.user.CustomUserDetails;
import com.aston.stockapp.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Controller
public class PortfolioController {

    @Autowired private PortfolioService portfolioService;
    @Autowired private YahooFinanceService yahooFinanceService;

    @GetMapping("/portfolio")
    public String viewPortfolio(Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication != null && authentication.isAuthenticated()) {
            // Use authentication to check and cast to CustomUserDetails
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                Long currentUserId = userDetails.getUser().getId();

                // Fetch portfolio for the logged in user and add to model
                Portfolio portfolio = portfolioService.getPortfolio(currentUserId);
                model.addAttribute("portfolio", portfolio);
                model.addAttribute("totalCost", portfolio.getTotalCost());
                model.addAttribute("totalValue", portfolio.getTotalValue());
                model.addAttribute("totalChangePercent", portfolio.getTotalChangePercent());
                portfolioService.getCurrentUserBalance().ifPresent(balance -> model.addAttribute("balance", balance));
                model.addAttribute("isEditing", false);
                return "portfolio";
            } else {
                // if the principal is not expected type for some reason, redirect to login
                redirectAttributes.addFlashAttribute("loginAlert", "Unexpected user details type. Please login again.");
                return "redirect:/login";
            }
        } else {
            // For unauthenticated users add attribute and redirect to login page
            redirectAttributes.addFlashAttribute("loginAlert", "You need to login to view your portfolio");
            return "redirect:/login";
        }
    }

    @PostMapping("/portfolio/add")
    public String addToPortfolio(@RequestParam String symbol, @RequestParam int quantity, @RequestParam(required = false) BigDecimal customPrice, @RequestParam String action, RedirectAttributes redirectAttributes) {
        try {
            BigDecimal finalPrice = customPrice != null && customPrice.compareTo(BigDecimal.ZERO) > 0 ? customPrice : yahooFinanceService.fetchStockData(symbol).getPrice();
            int adjustedQuantity = "sell".equalsIgnoreCase(action) ? -quantity : quantity;
            boolean isBuying = "buy".equalsIgnoreCase(action);

            portfolioService.addStockToPortfolio(symbol, adjustedQuantity, finalPrice, isBuying);

            if (isBuying) {
                redirectAttributes.addFlashAttribute("transactionSuccess", "Success: Bought " + quantity + " of " + symbol + ".");
            } else {
                redirectAttributes.addFlashAttribute("transactionSuccess", "Success: Sold " + quantity + " of " + symbol + ".");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("transactionError", "Error processing transaction for " + symbol + ": " + e.getMessage());
        }
        return "redirect:/stocks/" + symbol;
    }

//    @PostMapping("/portfolio/add")
//    public String addToPortfolio(@RequestParam String symbol, @RequestParam int quantity, @RequestParam(required = false) BigDecimal customPrice, @RequestParam String action, RedirectAttributes redirectAttributes) {
//        try {
//            BigDecimal finalPrice = customPrice != null && customPrice.compareTo(BigDecimal.ZERO) > 0 ? customPrice : yahooFinanceService.fetchStockData(symbol).getPrice();
//            int adjustedQuantity = "sell".equalsIgnoreCase(action) ? -quantity : quantity;
//
//            CompletableFuture<Void> transactionResult = portfolioService.addStockToPortfolio(symbol, adjustedQuantity, finalPrice);
//            transactionResult.join();
//
//            if ("sell".equalsIgnoreCase(action)) {
//                redirectAttributes.addFlashAttribute("transactionSuccess", "Success: Sold " + quantity + " of " + symbol + ".");
//            } else {
//                redirectAttributes.addFlashAttribute("transactionSuccess", "Success: Bought " + quantity + " of " + symbol + ".");
//            }
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("transactionError", "Error processing transaction for " + symbol + ": " + e.getMessage());
//        }
//        return "redirect:/stocks/" + symbol;
//    }

    @GetMapping("/portfolio/edit")
    public String editPortfolio(Model model) {
        try {
            Long userId = portfolioService.getCurrentUser();
            Portfolio portfolio = portfolioService.getPortfolio(userId);
            model.addAttribute("portfolio", portfolio);
            model.addAttribute("totalCost", portfolio.getTotalCost());
            model.addAttribute("totalValue", portfolio.getTotalValue());
            model.addAttribute("totalChangePercent", portfolio.getTotalChangePercent());
            model.addAttribute("isEditing", true);
            return "portfolio";
        } catch (IllegalStateException e) {
            // TODO: Log the error or handle it
            return "redirect:/login";
        }
    }

//    @PostMapping("/portfolio/transaction")
//    public String savePortfolio(@RequestParam String symbol, @RequestParam int quantity, @RequestParam String action, Model model, Authentication authentication) {
//        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
//            return "redirect:/login";
//        }
//        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//        Long userId = userDetails.getUser().getId();
//
//        boolean isBuying = "buy".equalsIgnoreCase(action);
//        try {
//            // Synchronously call the updateStockInPortfolio method
//            portfolioService.updateStockInPortfolio(userId, symbol, quantity, isBuying);
//
//            // Prepare the success message
//            String message = isBuying ? "Successfully purchased " : "Successfully sold ";
//            message += quantity + " units of " + symbol + ".";
//            model.addAttribute("transactionSuccess", message);
//        } catch (Exception e) {
//            model.addAttribute("transactionError", "Error processing transaction for " + symbol + ".");
//        }
//
//        return "redirect:/stocks/" + symbol;
//    }

    @PostMapping("/portfolio/update")
    public String updatePortfolioItem(@RequestParam("itemId") Long itemId, @RequestParam("quantity") int quantity) {
        portfolioService.updatePortfolioItem(itemId, quantity);
        return "redirect:/portfolio";
    }
}