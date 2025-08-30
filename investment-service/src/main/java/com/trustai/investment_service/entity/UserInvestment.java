package com.trustai.investment_service.entity;

import com.trustai.common.enums.CalculationType;
import com.trustai.investment_service.enums.InvestmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
| Field Name               | Usage / Purpose                                                                 |
|--------------------------|----------------------------------------------------------------------------------|
| `id`                     | Primary key for the investment record.                                          |
| `userId`                 | User who owns the investment. Used for user-level filtering and queries.        |
| `schema`                 | Reference to the `InvestmentSchema` entity; defines the terms of the investment.|
| `investedAmount`         | The principal amount the user has invested.                                     |
| `perPeriodProfit`        | Calculated profit for each payout period.                                       |
| `expectedTotalReturnAmount` | Total profit expected from the investment (excluding capital, unless capitalBack is true). |
| `receivedReturnAmount`   | Total profit already credited to the user.                                      |
| `capitalReturned`        | Whether the capital has already been returned to the user.                      |
| `isCancelled`            | Indicates whether the investment was cancelled by the user or system.           |
| `cancelledAt`            | Timestamp when the investment was cancelled.                                    |
| `subscribedAt`           | Timestamp when the investment was created/subscribed.                           |
| `nextPayoutAt`           | Scheduled date of the next return credit to the user.                           |
| `maturityAt`             | Date on which the investment will complete all its return cycles.               |
| `lastPayoutAt`           | Date of the last credited profit. Helps in audit logs and scheduling.           |
| `finalReturnAmount`      | Total final credited amount (profit + capital if applicable); used for summary. |
| `cancelReason`           | Optional note explaining why user/system cancelled the investment.              |
| `txnRefId`               | Wallet transaction ID that created the investment; useful for tracing/deductions.|
| `capitalAmountReturned`  | Tracks how much capital has been returned (useful for partial returns).         |
| `earnedPeriods`          | Number of completed payout cycles. Used to optimize UI & reports.               |
| `profitCalculationType`  | Stores calculation mode (`FLAT` or `PERCENTAGE`) at time of investment.         |
| `isReinvested`           | True if this investment was created by reinvesting from another investment.     |
| `status`                 | Enum showing current investment status: `ACTIVE`, `CANCELLED`, or `COMPLETED`.  |

 */

@Entity
@Table(name = "user_investments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInvestment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Basic user association
    @Column(nullable = false)
    private Long userId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "schema_id", nullable = false)
    private InvestmentSchema schema;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal investedAmount;

    @Column(precision = 19, scale = 4)
    private BigDecimal perPeriodProfit; // profit earned in each return period

    @Column(precision = 19, scale = 4)
    private BigDecimal expectedTotalReturnAmount;

    @Column(precision = 19, scale = 4)
    private BigDecimal receivedReturnAmount = BigDecimal.ZERO;

    private boolean capitalReturned = false;

    private boolean isCancelled = false;
    private LocalDateTime cancelledAt;

    private LocalDateTime subscribedAt;
    private LocalDateTime nextPayoutAt;
    private LocalDateTime maturityAt;

    private LocalDateTime lastPayoutAt; // Tracks the last successful profit payout. Useful for monitoring and audit logs.
    private BigDecimal finalReturnAmount; // Captures the exact final return (profit + capital) for historical reports.
    private String cancelReason; // Reason for cancellation (if any). Helps in analytics and user feedback.
    private String txnRefId; // Reference to the transaction ID used for investment deduction. Useful for traceability.
    private BigDecimal capitalAmountReturned; // If capital is partially returned or returned in steps, this helps track it.
    private int earnedPeriods; // Cache of completed profit cycles (for quick UI display or analytics).
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CalculationType profitCalculationType; // Can store FLAT or PERCENTAGE at investment time, even if schema later changes.
    private boolean isReinvested; // Indicates if this investment is auto-renewed or reinvested into another plan.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvestmentStatus status;

    @PrePersist
    protected void onCreate() {
        this.subscribedAt = LocalDateTime.now();
        this.status = InvestmentStatus.ACTIVE;
    }
}
