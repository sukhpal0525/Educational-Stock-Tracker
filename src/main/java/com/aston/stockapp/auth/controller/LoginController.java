package com.aston.stockapp.auth.controller;

import com.aston.stockapp.user.User;
import com.aston.stockapp.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@Slf4j
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String login(Model model, @RequestParam Optional<String> error, @RequestParam Optional<String> logout) {
        model.addAttribute("user", new User());
        if (error.isPresent()) {
            model.addAttribute("errorMsg", "Invalid login details.");
        }
        if (logout.isPresent()) {
            model.addAttribute("msg", "Logged out successfully.");
        }
        return "login";
    }

//    @PostMapping
//    public String login(@ModelAttribute("user") User user, RedirectAttributes redirectAttrs) {
//        // login logic
//
//
//        User dbUser = userRepository.findByUsername(user.getUsername());
//        log.info("Login User: {}, DB User: {}", user, dbUser);
//        if (dbUser == null) {
//            // do something
//        }
//
//        // Verify user is who they say they are
//
//
//        return "login";
//    }
}