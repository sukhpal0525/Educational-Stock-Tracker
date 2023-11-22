package com.aston.stockapp.user;

import lombok.Getter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "Role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoleID")
    private Long id;

    @Getter
    @Enumerated(EnumType.STRING)
    private RoleName name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    public void setName(RoleName name) {
        this.name = name;
    }

    public enum RoleName { USER, ADMIN }
}