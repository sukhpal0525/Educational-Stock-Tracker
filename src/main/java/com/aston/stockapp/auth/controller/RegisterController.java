package com.aston.stockapp.auth.controller;

import com.aston.stockapp.domain.portfolio.Portfolio;
import com.aston.stockapp.domain.portfolio.PortfolioRepository;
import com.aston.stockapp.user.User;
import com.aston.stockapp.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/register")
public class RegisterController {

    @Autowired private UserRepository userRepository;
    @Autowired private PortfolioRepository portfolioRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @GetMapping
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping
    public String processRegistration(@RequestParam String username, @RequestParam String password, @RequestParam String confirmPassword, RedirectAttributes redirectAttrs) {
        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            redirectAttrs.addFlashAttribute("errorMsg", "Error: Both passwords need to match.");
            return "redirect:/register";
        }
        // If user already exists:
        if (userRepository.findByUsername(username) != null) {
            redirectAttrs.addFlashAttribute("errorMsg", "Error: This username has already been taken.");
            return "redirect:/register";
        }
        // Proceed with the registration
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user = userRepository.save(user); // Save the user to generate an ID

        // Ensure a portfolio is created for every new user
        Portfolio portfolio = new Portfolio();
        portfolio.setUser(user); // Associate the portfolio with the user
        portfolio.setTotalCost(0.0);
        portfolio.setTotalValue(0.0);
        portfolio.setTotalChangePercent(0.0);
        portfolioRepository.save(portfolio);

        redirectAttrs.addFlashAttribute("successMsg", "Success: Your account has been registered. Please login.");
        return "redirect:/login";
    }
}