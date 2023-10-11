package com.aston.stockapp.account.user;

import com.aston.stockapp.account.auth.model.User;
import com.aston.stockapp.account.auth.model.AuthRole;

public class UserProfileService {

    public String getUserRole(User user) {
        if (user.getRoles().contains(AuthRole.RoleName.ADMIN)) {
            return "Admin";
        } else {
            return "User";
        }
    }
}
