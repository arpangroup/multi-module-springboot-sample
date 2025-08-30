package com.trustai.common.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // e.g. ROLE_USER, ROLE_ADMIN
    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    public Role(String name) {
        this.name = name;
    }
}