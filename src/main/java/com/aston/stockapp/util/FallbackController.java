package com.aston.stockapp.util;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FallbackController {

    @RequestMapping("/**")
    public String fallbackHandler(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("searchFailed", "The requested page doesn't exist.");
        return "redirect:/";
    }
}