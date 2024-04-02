package com.aston.stockapp.domain.order;

import com.aston.stockapp.domain.portfolio.PortfolioService;
import com.aston.stockapp.domain.transaction.Transaction;
import com.aston.stockapp.domain.transaction.TransactionRepository;
import com.aston.stockapp.user.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired private PortfolioService portfolioService;
    @Autowired private TransactionRepository transactionRepository;

    @GetMapping("")
    public String getOrders(@RequestParam(required = false) String ticker, Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getUser().getId();

            List<Transaction> transactions;
            if (ticker != null && !ticker.isEmpty()) {
                transactions = transactionRepository.findByUserIdAndStockTicker(userId, ticker.toUpperCase());
            } else {
                transactions = transactionRepository.findByUserId(userId);
            }

            model.addAttribute("transactions", transactions);
            model.addAttribute("ticker", ticker);
            return "orders";
        } else {
            return "redirect:/login";
        }
    }
}

