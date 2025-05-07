package org.ncu.mf_loan_system.entities;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;



@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {
    public static final String LOAN_OFFICER = "ROLE_LOAN_OFFICER";
    public static final String MANAGER = "ROLE_MANAGER";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, unique = true)
    private String name;

    public Role() {}

    public Role(String name) {
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}