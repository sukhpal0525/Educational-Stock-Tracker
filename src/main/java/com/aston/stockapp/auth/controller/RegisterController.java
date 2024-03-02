package com.aston.stockapp.auth.controller;

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
        userRepository.save(user);

        redirectAttrs.addFlashAttribute("successMsg", "Success: Your account has been registered. Please login.");
        return "redirect:/login";
    }
}