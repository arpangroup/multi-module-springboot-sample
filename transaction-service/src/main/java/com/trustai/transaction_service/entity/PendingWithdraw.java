package com.trustai.transaction_service.entity;

import com.trustai.common.utils.RequestContextHolderUtils;
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

    @Column(unique = true, nullable = false)
    private String txnRefId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private BigDecimal amount;

    private BigDecimal serviceCharge;
    private String walletAddress;
    private String rankCode;
    private boolean isProfitWallet;

    @Enumerated(EnumType.STRING)
    private WithdrawStatus status = WithdrawStatus.PENDING;

    private String remarks;
    private String rejectionReason;

    private String approvedBy;
    private LocalDateTime approvedAt;
    private String rejectedBy;
    private LocalDateTime rejectedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false, updatable = true)
    private LocalDateTime updatedAt;
    @Column
    private String createdBy;
    @Column
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        this.createdBy = getCurrentUsername();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = getCurrentUsername();
    }

    public enum WithdrawStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    private String getCurrentUserId() {
        return RequestContextHolderUtils.getCurrentUserId() + "";
    }

    private String getCurrentUsername() {
        return RequestContextHolderUtils.getCurrentUsername();
    }
}
