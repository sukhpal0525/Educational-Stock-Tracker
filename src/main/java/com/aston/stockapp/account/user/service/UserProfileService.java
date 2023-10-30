package com.aston.stockapp.account.user.service;

import com.aston.stockapp.account.auth.model.Role;
import com.aston.stockapp.account.auth.model.User;

public class UserProfileService {

    public String getUserRole(User user) {
        if (user.getRoles().contains(Role.RoleName.ADMIN)) {
            return "Admin";
        } else {
            return "User";
        }
    }
}
