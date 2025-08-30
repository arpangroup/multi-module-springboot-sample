package com.trustai.common.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "otp_sessions")
@Data
@NoArgsConstructor
public class OtpSessionEntity {

    @Id
    private String sessionId;

    private String username;
    private String flow;
    private String otp;

    private int attempts;

    private long createdAt;

    private boolean valid;

    @Column(name = "locked_until")
    private Long lockedUntil; // epoch millis

    public OtpSessionEntity(String sessionId, String username, String flow, String otp, long createdAt) {
        this.sessionId = sessionId;
        this.username = username;
        this.flow = flow;
        this.otp = otp;
        this.createdAt = createdAt;
        this.valid = true;
        this.attempts = 0;
    }
}
