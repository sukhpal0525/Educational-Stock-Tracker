package com.aston.stockapp.domain.admin;

import com.aston.stockapp.domain.portfolio.Portfolio;
import com.aston.stockapp.domain.portfolio.PortfolioService;
import com.aston.stockapp.domain.transaction.Transaction;
import com.aston.stockapp.domain.transaction.TransactionRepository;
import com.aston.stockapp.user.User;
import com.aston.stockapp.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserRepository userRepository;
    @Autowired private UserRepository portfolioRepository;
    @Autowired private PortfolioService portfolioService;
    @Autowired private TransactionRepository transactionRepository;

    @GetMapping("")
    public String dashboard(@RequestParam(required = false) String view, Model model, Pageable pageable, Authentication authentication, RedirectAttributes redirectAttributes) {
        // Check if user is authenticated and has ADMIN role
        if (authentication == null || !authentication.isAuthenticated() || authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            redirectAttributes.addFlashAttribute("errorMsg", "You must be logged in as an admin to view this page.");
            return "redirect:/login";
        }

        long userCount = userRepository.count();
        long portfolioCount = portfolioRepository.count();
        long transactionCount = transactionRepository.count();

        if (view == null || view.isEmpty()) {
            view = "users";
        }

        if ("users".equals(view)) {
            Page<User> usersPage = userRepository.findAll(pageable);
            model.addAttribute("usersPage", usersPage);
        } else if ("portfolios".equals(view)) {
            Page<Portfolio> portfoliosPage = portfolioService.getAllPortfolios(pageable);
            model.addAttribute("portfoliosPage", portfoliosPage);
        } else if ("transactions".equals(view)) {
            Page<Transaction> transactionsPage = transactionRepository.findAll(pageable);
            model.addAttribute("transactionsPage", transactionsPage);
        }

        model.addAttribute("userCount", userCount);
        model.addAttribute("portfolioCount", portfolioCount);
        model.addAttribute("transactionCount", transactionCount);
        model.addAttribute("view", view);
        return "admin-dashboard";
    }

    @DeleteMapping("/deleteUser/{userId}")
    public @ResponseBody String deleteUser(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        try {
            userRepository.deleteById(userId);
            return "User successfully deleted";
        } catch (Exception e) {
            return "Error deleting user: " + e.getMessage();
        }
    }
}