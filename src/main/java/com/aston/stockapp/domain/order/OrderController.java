package com.aston.stockapp.domain.order;

import com.aston.stockapp.domain.transaction.Transaction;
import com.aston.stockapp.domain.transaction.TransactionRepository;
import com.aston.stockapp.user.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired private TransactionRepository transactionRepository;

    @GetMapping("")
    public String getOrders(@RequestParam(required = false) String ticker, @RequestParam(required = false) String transactionType, Authentication authentication, Model model, @PageableDefault(size = 12) Pageable pageable) {
        if (authentication != null && authentication.isAuthenticated()) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getUser().getId();
            Page<Transaction> transactionsPage;

            boolean anyTransactionType = transactionType == null || transactionType.isEmpty() || "Any".equals(transactionType);

            if (!anyTransactionType && ticker != null && !ticker.isEmpty()) {
                transactionsPage = transactionRepository.findByUserIdAndStockTickerAndTransactionType(userId, ticker.toUpperCase(), transactionType, pageable);
            } else if ("Edit".equals(transactionType)) {
                // Include "Edit", "Edit (Buy)", and "Edit (Sell)" transactions
                transactionsPage = transactionRepository.findByUserIdAndEditTypes(userId, pageable);
            } else if (!anyTransactionType) {
                transactionsPage = transactionRepository.findByUserIdAndTransactionType(userId, transactionType, pageable);
            } else if (ticker != null && !ticker.isEmpty()) {
                transactionsPage = transactionRepository.findByUserIdAndStockTicker(userId, ticker.toUpperCase(), pageable);
            } else {
                transactionsPage = transactionRepository.findByUserId(userId, pageable);
            }

            model.addAttribute("transactionsPage", transactionsPage);
            model.addAttribute("ticker", ticker);
            model.addAttribute("transactionType", transactionType);
            model.addAttribute("hasTransactions", transactionsPage.hasContent());

            return "orders";
        } else {
            return "redirect:/login";
        }
    }


//    @GetMapping("")
//    public String getOrders(@RequestParam(required = false) String ticker, @RequestParam(required = false) String transactionType, Authentication authentication, Model model, @PageableDefault(size = 12) Pageable pageable) {
//        if (authentication != null && authentication.isAuthenticated()) {
//            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//            Long userId = userDetails.getUser().getId();
//            Page<Transaction> transactionsPage;
//
//            // Check for "Any" transaction type or when no transaction type is specified
//            boolean anyTransactionType = transactionType == null || transactionType.isEmpty() || "Any".equals(transactionType);
//
//            if (!anyTransactionType && ticker != null && !ticker.isEmpty()) {
//                // Filter by both ticker and transaction type
//                transactionsPage = transactionRepository.findByUserIdAndStockTickerAndTransactionType(userId, ticker.toUpperCase(), transactionType, pageable);
//            } else if (!anyTransactionType) {
//                // Filter by transaction type only
//                transactionsPage = transactionRepository.findByUserIdAndTransactionType(userId, transactionType, pageable);
//            } else if (ticker != null && !ticker.isEmpty()) {
//                // Filter by ticker only
//                transactionsPage = transactionRepository.findByUserIdAndStockTicker(userId, ticker.toUpperCase(), pageable);
//            } else {
//                // Show all transactions by default
//                transactionsPage = transactionRepository.findByUserId(userId, pageable);
//            }
//
//            model.addAttribute("transactionsPage", transactionsPage);
//            model.addAttribute("ticker", ticker);
//            model.addAttribute("transactionType", transactionType);
//            model.addAttribute("hasTransactions", transactionsPage.hasContent());
//
//            return "orders";
//        } else {
//            return "redirect:/login";
//        }
//    }
}

