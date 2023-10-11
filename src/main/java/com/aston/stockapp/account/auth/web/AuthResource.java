package com.aston.stockapp.account.auth.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthResource {
    
    @GetMapping("/index")
    public String home() {
        return "index";
    }
}