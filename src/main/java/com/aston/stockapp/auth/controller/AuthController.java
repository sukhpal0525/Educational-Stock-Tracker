package com.aston.stockapp.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class AuthController {

    @GetMapping("/")
    public String index(@ModelAttribute("modalMessage") String modalMessage, Model model) {
        if (!modalMessage.isEmpty()) {
            model.addAttribute("modalMessage", modalMessage);
        }
        return "index";
    }
}