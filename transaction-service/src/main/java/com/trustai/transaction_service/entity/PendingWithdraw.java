package com.trustai.transaction_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pending_withdraw")
@NoArgsConstructor
@Accessors(chain = true)
@Data
public class PendingWithdraw {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private WithdrawStatus status = WithdrawStatus.PENDING;

    private String remarks;
    private String rejectionReason;

    private String approvedBy;
    private LocalDateTime approvedAt;
    private String rejectedBy;
    private LocalDateTime rejectedAt;

    @Column(nullable = false, updatable = false) private LocalDateTime createdAt;
    @Column(nullable = false) private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum WithdrawStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
