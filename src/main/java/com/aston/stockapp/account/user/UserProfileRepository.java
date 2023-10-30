package com.aston.stockapp.account.user;

import java.util.List;
import java.util.Optional;

import com.aston.stockapp.account.auth.model.Role;
import com.aston.stockapp.account.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends JpaRepository<User, Long> {

//    List<User> findAll();
//    Role findByName(String name);
//    User findByUsername(String username);
//  Optional<User> findOneByEmail(String email);
//  Optional<User> findByUsername(String username);
}
