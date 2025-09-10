package com.trustai.investment_service.entity;

import com.trustai.common.enums.CurrencyType;
import com.trustai.investment_service.enums.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "investment_schemas")
@Data
@NoArgsConstructor
public class InvestmentSchema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // For distinguishing between regular investment and stake
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvestmentType investmentType = InvestmentType.STANDARD;


    private String linkedRank = "RANK_0"; // optional, to match customers requirement
    @Column(nullable = false, unique = true)
    private String name;
    private String schemaBadge;
    private String imageUrl;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SchemaType schemaType = SchemaType.RANGE;

    // Pricing:
    //@Column(precision = 19, scale = 4)
    private BigDecimal stakePrice; // default investment price
    @Column(name = "min_invest_amt", precision = 19, scale = 4)
    private BigDecimal minimumInvestmentAmount = BigDecimal.ZERO;
    @Column(name = "max_invest_amt", precision = 19, scale = 4)
    private BigDecimal maximumInvestmentAmount = BigDecimal.ZERO;

    // Withdraw:
    @Column(precision = 19, scale = 4)
    private BigDecimal minimumWithdrawalAmount = BigDecimal.ZERO;
    @Column(precision = 19, scale = 4)
    private BigDecimal handlingFee = BigDecimal.ZERO;

    // Return:
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnType returnType = ReturnType.PERIOD;
    @Column(precision = 19, scale = 4)
    private BigDecimal returnRate = BigDecimal.ZERO;
    private int totalReturnPeriods; // totalReturnPeriods
    @ManyToOne(optional = false)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule returnSchedule;
    private boolean isCapitalReturned;

    // Cancellation:
    private boolean isCancellable;
    private int cancellationGracePeriodMinutes;
    private BigDecimal earlyExitPenalty; // Penalty if exited before full duration

    // Optional:
    private boolean isFeatured;
    private boolean isTradeable;
    private String description; // Schema summary for UI/API display
    private String termsAndConditionsUrl; // For linking external T&C

    // Calculation:
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterestCalculationType interestCalculationMethod = InterestCalculationType.PERCENTAGE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyType currency = CurrencyType.USDT; // e.g., USD, INR – especially if multi-currency support is needed


    private boolean isActive;




    // ########################### PAYOUT #####################################
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayoutMode payoutMode = PayoutMode.DAILY;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "investment_schema_payout_days", joinColumns = @JoinColumn(name = "schema_id"))
    @Column(name = "day_of_week")
    private Set<DayOfWeek> payoutDays; // For WEEKLY mode ==> Enum: MONDAY, TUESDAY, ...

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "investment_schema_payout_dates", joinColumns = @JoinColumn(name = "schema_id"))
    @Column(name = "day_of_month")
    private Set<Integer> payoutDates; //  For MONTHLY mode ==> Valid values: 1 to 31
    // ########################### ./PAYOUT #####################################

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "investment_schema_participation_levels", joinColumns = @JoinColumn(name = "schema_id"))
    @Column(name = "required_level")
    private Set<String> participationLevels;


    @Column(nullable = false, updatable = false) private LocalDateTime createdAt;
    @Column(nullable = false, updatable = true) private LocalDateTime updatedAt;
    @Column private String createdBy;
    @Column private String updatedBy;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
