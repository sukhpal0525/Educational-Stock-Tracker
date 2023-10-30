package com.aston.stockapp.account.auth.repository;

import com.aston.stockapp.account.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<User, Long> {
}
