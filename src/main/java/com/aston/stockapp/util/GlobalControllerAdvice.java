package com.aston.stockapp.util;

import com.aston.stockapp.user.User;
import com.aston.stockapp.user.repository.UserRepository;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.security.Principal;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired private UserRepository userRepository;

    @ModelAttribute("balance")
    public BigDecimal getCurrentUserBalance(Principal principal) {
        if (principal != null) {
            User user = userRepository.findByUsername(principal.getName());
            if (user != null) {
                return user.getBalance();
            }
        }
        return BigDecimal.ZERO;
    }
}
