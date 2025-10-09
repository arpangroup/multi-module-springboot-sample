package com.trustai.userservice.user.registration;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "pending_users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingUser {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(unique = true, nullable = false)
    private String email;

    private String mobile;
    private String referralCode;

//    @Column(nullable = false)
//    private String verificationToken;
//
//    @Column(nullable = false)
//    private LocalDateTime tokenExpiry;

    private LocalDateTime createdAt = LocalDateTime.now();
}
