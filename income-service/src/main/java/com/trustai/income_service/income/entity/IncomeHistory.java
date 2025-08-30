package com.trustai.income_service.income.entity;

import com.trustai.common.enums.IncomeType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "income_history", indexes = {
        @Index(name = "idx_income_type_created_at", columnList = "incomeType, createdAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncomeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId; // Recipient
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private IncomeType incomeType; // "DAILY" or "TEAM"

    private String note; // e.g. "From user 42, RANK_2"

    private Long sourceUserId; // Who triggered this income

    private String sourceUserRank;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public IncomeHistory(Long userId, BigDecimal amount, String sourceUserRank) {
        this.userId = userId;
        this.amount = amount;
        this.incomeType = IncomeType.TEAM;
        this.sourceUserRank = sourceUserRank;
    }
}
