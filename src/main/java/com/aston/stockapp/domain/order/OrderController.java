package com.aston.stockapp.domain.order;

import com.aston.stockapp.domain.portfolio.PortfolioService;
import com.aston.stockapp.domain.transaction.TransactionRepository;
import com.aston.stockapp.user.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired private PortfolioService portfolioService;
    @Autowired private TransactionRepository transactionRepository;

    @GetMapping("")
    public String getOrders(String symbol, Authentication authentication, Model model) {
        // Check if the user is authenticated to populate user-related data
        if (authentication != null && authentication.isAuthenticated()) {
            Long userId = ((CustomUserDetails) authentication.getPrincipal()).getUser().getId();
            model.addAttribute("ownsStock", portfolioService.ownsStock(symbol));
            model.addAttribute("transactions", transactionRepository.findByUserId(userId));
            model.addAttribute("quantity", portfolioService.getPortfolio(userId).getItems());
            portfolioService.getCurrentUserBalance().ifPresent(balance -> model.addAttribute("balance", balance));
        }
        return "orders";
    }
}

