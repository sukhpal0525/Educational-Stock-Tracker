package com.aston.stockapp.account.auth.controller;

import com.aston.stockapp.account.auth.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.aston.stockapp.account.auth.repository.UserRepository;

public class RegisterController {

    @Controller
    @RequestMapping("/register")
    public class RegistrationController {

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private UserRepository UserRepository;

        @GetMapping
        public String register() {
            return "register";
        }

        @PostMapping
        public String registerUser(@RequestParam String username, @RequestParam String password) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            UserRepository.save(user);
            return "redirect:/login";
        }
    }

}
