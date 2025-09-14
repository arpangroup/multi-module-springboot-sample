package com.trustai.userservice.user.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_attempts")
@Data
@NoArgsConstructor
public class PasswordResetAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String ipAddress;
    private int attempts = 0;
    private LocalDateTime lastAttempt;
    private LocalDateTime lockedUntil;

    public PasswordResetAttempt(String email) {
        this.email = email;
    }

    public PasswordResetAttempt(String email, String ipAddress) {
        this.email = email;
        this.ipAddress = ipAddress;
        this.attempts = 0;
    }

    public void incrementAttempts() {
        this.attempts++;
    }

    public void incrementAttempts(int maxAttempts, Duration lockDuration) {
        this.attempts++;
        if (this.attempts >= maxAttempts) {
            this.lockedUntil = LocalDateTime.now().plus(lockDuration);
        }
    }

    public void reset() {
        this.attempts = 0;
        this.lockedUntil = null;
    }

    public void lock(Duration duration) {
        this.lockedUntil = LocalDateTime.now().plus(duration);
    }

    public boolean isLocked() {
        return lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now());
    }

}
