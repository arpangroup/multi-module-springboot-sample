package com.trustai.common.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_tokens", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"target", "type"})
})
@NoArgsConstructor
@AllArgsConstructor
@Data
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationType type; // EMAIL or MOBILE

    @Column(nullable = false)
    private String target; // email or mobile

    @Column(nullable = false, length = 10)
    private String code;

    private String token; //UUID string <--Required for Email Link Verification

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private boolean verified = false;

    private LocalDateTime createdAt = LocalDateTime.now();

}
