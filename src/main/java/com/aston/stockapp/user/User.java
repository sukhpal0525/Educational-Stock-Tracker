package com.aston.stockapp.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.aston.stockapp.util.PasswordEncoderDeserializer;
import lombok.Data;
import javax.persistence.Table;
import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import java.util.Set;

@Entity
@Data
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private Long id;

    @Column(nullable = false, columnDefinition = "tinyint(1) default 0")
    private boolean isAdmin;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "User_Roles",
            joinColumns = @JoinColumn(name = "UserID"),
            inverseJoinColumns = @JoinColumn(name = "RoleID")
    )
    private Set<Role> roles;

    @Column(name = "Username", nullable = false, unique = true)
    private String username;

    @Column(name = "Password", nullable = false)
    private String password;

    @JsonDeserialize(using = PasswordEncoderDeserializer.class)
    public void setPassword(String password) { this.password = password; }
}