package com.aston.stockapp.account.auth.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.aston.stockapp.util.PasswordEncoderDeserializer;
import lombok.Data;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import java.util.Set;

@Entity
@Data
@Table(name = "WebUser")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private Long id;

    @Column(name = "UserName", nullable = false)
    private String username;

    @Column(name = "Password", nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<AuthRole> roles;

    @JsonDeserialize(using = PasswordEncoderDeserializer.class)
    public void setPassword(String password) { this.password = password; }
}