package com.trustai.transaction_service.entity;

import com.trustai.common.enums.PaymentGateway;
import com.trustai.common.enums.TransactionType;
import com.trustai.transaction_service.util.TransactionIdGenerator;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@NoArgsConstructor
@Getter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = true)
    @Setter
    private Long senderId;

    @Column(nullable = false)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = true)
    @Setter
    private String txnRefId; // should be unique

    @Column(length = 255, nullable = true)
    @Setter
    private String remarks;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(nullable = true)
    @Setter
    private BigDecimal txnFee;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType txnType;

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
    @Setter
    private String linkedTxnId;// Useful for reversals, refunds, or when one transaction triggers another (e.g., commission, bonus, chargebacks).

    @Column(length = 50)
    @Setter
    private String sourceModule; // e.g., "investment", "referral", "marketplace" <== If transactions can originate from different modules

    @Column(nullable = false)
    private boolean isCredit;

    @Column(nullable = false)
    private boolean isDeleted = false; // Useful for compliance, data retention policies without deleting.


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
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Transaction(long userId, BigDecimal amount, @NotNull TransactionType transactionType, BigDecimal balance, boolean isCredit) {
        this.userId = userId;
        this.amount = amount;
        this.txnType = transactionType;
        this.balance = balance == null ? BigDecimal.ZERO : balance;
        this.gateway = PaymentGateway.SYSTEM;
        this.txnRefId = TransactionIdGenerator.generateTransactionId();
        this.isCredit = isCredit;
    }

    public enum TransactionStatus {
        PENDING,
        SUCCESS,
        FAILED,
        CANCELLED;
    }
}
