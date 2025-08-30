package com.trustai.transaction_service.entity;

import com.trustai.common.enums.PaymentGateway;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pending_deposit")
@NoArgsConstructor
@Accessors(chain = true)
@Data
public class PendingDeposit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(nullable = false, unique = true)
    private String txnRefId; // should be unique

    @Column(length = 255, nullable = true)
    @Setter
    private String remarks;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DepositStatus status = DepositStatus.PENDING;

    @Column(nullable = true)
    @Setter
    private BigDecimal txnFee = BigDecimal.TWO;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Setter
    private PaymentGateway gateway = PaymentGateway.SYSTEM;

    @Column(nullable = true)
    @Setter
    private String metaInfo;

    @Column(length = 3, nullable = false)
    @Setter
    private String currencyCode = "INR";

    @Column
    private String linkedTxnId;// Useful for reversals, refunds, or when one transaction triggers another (e.g., commission, bonus, chargebacks).

    @Column(nullable = false)
    private boolean isDeleted = false; // Useful for compliance, data retention policies without deleting.


    @Column(nullable = false, updatable = false) private LocalDateTime createdAt;
    @Column(nullable = false, updatable = true) private LocalDateTime updatedAt;
    @Column private String createdBy;
    @Column private String updatedBy;
    @Column private String approvedBy;
    @Column private LocalDateTime approvedAt;
    @Column private String rejectedBy;
    @Column private LocalDateTime rejectedAt;

    @Column(length = 255)
    private String rejectionReason;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public PendingDeposit(long userId, BigDecimal amount) {
        this.userId = userId;
        this.amount = amount;
    }

    // For Manual Deposit:
    public PendingDeposit(long userId, BigDecimal amount, String linkedAccountNumber) {
        this.userId = userId;
        this.amount = amount;
        this.linkedTxnId = linkedAccountNumber;
    }

    // For Standard Deposit:
    public PendingDeposit(long userId, BigDecimal amount, PaymentGateway paymentGateway, String txnRefId, BigDecimal txnFee) {
        this.userId = userId;
        this.amount = amount;
        this.txnRefId = txnRefId;
        this.txnFee = txnFee;
    }

    public enum DepositStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
