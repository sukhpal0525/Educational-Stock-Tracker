package com.aston.stockapp.auth.controller;

import com.aston.stockapp.user.User;
import com.aston.stockapp.user.repository.UserRepository;
import com.aston.stockapp.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping
    public String processRegistration(@ModelAttribute("user") User user, RedirectAttributes redirectAttrs) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // If user already exists:
        if (userRepository.findByUsername(user.getUsername()) != null) {
            redirectAttrs.addFlashAttribute("errorMsg", "Username already exists.");
            return "redirect:/register";
        }

        userRepository.save(user);
        redirectAttrs.addFlashAttribute("successMsg", "Registration successful. Please login.");
        return "redirect:/login";
    }
}
