package com.aston.stockapp.account.auth.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "AuthRole")
public class AuthRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleName name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    public RoleName getName() { return name; }
    public void setName(RoleName name) { this.name = name; }

    public enum RoleName { USER, ADMIN }
}
