package com.aston.stockapp.auth.service;

import com.aston.stockapp.user.User;
import com.aston.stockapp.user.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class AuthService {

    private UserRepository userRepository;

    public String register(User user, BindingResult result, RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("errorMsg", "Error: Invalid user details.");
            return "redirect:/register";
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        try {
            userRepository.save(user);
            redirectAttrs.addFlashAttribute("successMsg", "Registration successful!");
        } catch (DataIntegrityViolationException e) {
            redirectAttrs.addFlashAttribute("errorMsg", "Error: Username already exists.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMsg", "Unexpected error occurred. Please try again.");
        }
        return "redirect:/register";
    }
}