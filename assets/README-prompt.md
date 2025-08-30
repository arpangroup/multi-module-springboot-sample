## Reservation Design Suggestion

````text
I have below entities, I want to achieve daily resuvation of an stock or schema, so that user will get daily income if He reserve the stack and then if he sells, he will get an daily icome. A user can do maximum one reserve in a single day. user will able too see his reserved items in ui, for that create a controller to show reserved items. the reserve validity i.e., maturity or expiry day is maximum one day. after that if its not selled it will be moved to users collection.

is my approach correct, suggest better approach? can I user user_investment table to store reserved items also, or should we use a new entity to store the users reserved items.. user will be able to see only the active reserved items, once it expire and sell it will not show in users UI. Please use good practice to design this.

If my approoach is wrong suggest good approach alsofor the same. 


@Entity
@Table(name = "investment_schemas")
@Data
@NoArgsConstructor
public class InvestmentSchema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String linkedRank = "RANK_0"; // optional, to match customers requirement

    @Column(nullable = false, unique = true)
    private String title;
    private String schemaBadge;
    private String imageUrl;

    // For distinguishing between regular investment and stake
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvestmentSubType investmentSubType = InvestmentSubType.STANDARD;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SchemaType schemaType = SchemaType.RANGE;

    @Column(name = "min_invest_amt", precision = 19, scale = 4)
    private BigDecimal minimumInvestmentAmount = BigDecimal.ZERO;
    @Column(name = "max_invest_amt", precision = 19, scale = 4)
    private BigDecimal maximumInvestmentAmount = BigDecimal.ZERO;
    @Column(precision = 19, scale = 4)
    private BigDecimal handlingFee = BigDecimal.ZERO;
    @Column(precision = 19, scale = 4)
    private BigDecimal minimumWithdrawalAmount = BigDecimal.ZERO;

    @Column(precision = 19, scale = 4)
    private BigDecimal returnRate = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterestCalculationType interestCalculationMethod = InterestCalculationType.PERCENTAGE;

//    @OneToOne(optional = false)
//    @JoinColumn(name = "schedule_id", nullable = false)
    @ManyToOne(optional = false)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule returnSchedule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnType returnType = ReturnType.PERIOD;

    private int totalReturnPeriods; // totalReturnPeriods

    private boolean isCapitalReturned;
    private boolean isFeatured;

    private boolean isCancellable;
    private int cancellationGracePeriodMinutes;

    private boolean isTradeable;
    private boolean isActive;

    private String description; // Schema summary for UI/API display

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyType currency = CurrencyType.USDT; // e.g., USD, INR – especially if multi-currency support is needed
    private BigDecimal earlyExitPenalty; // Penalty if exited before full duration
    private String termsAndConditionsUrl; // For linking external T&C

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

    public enum InvestmentSubType {
        STANDARD,
        STAKE
    }
}


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


@Entity
@Table(name = "schedules") // ReturnSchedule
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String scheduleName;

    //private int scheduleInHour;
    @Column(nullable = false)
    private int intervalMinutes; // e.g., 1440 = 1 day, 10080 = 1 week

    private String description;

    public Duration getIntervalDuration() {
        return Duration.ofMinutes(this.intervalMinutes);
    }
}


````

## 1. ✅ Use a Separate Entity: UserReservation
````java
@Entity
@Table(name = "user_reservations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "reservation_date"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "schema_id")
    private InvestmentSchema schema;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal reservedAmount;

    private LocalDateTime reservedAt = LocalDateTime.now();
    private LocalDateTime expiryAt; // = reservedAt + 1 day

    private boolean isSold = false;
    private LocalDateTime soldAt;

    private BigDecimal incomeEarned = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDate reservationDate; // For unique constraint per day

    public boolean isActive() {
        return !isSold && LocalDateTime.now().isBefore(expiryAt);
    }
}

````

## ✅ Controller Endpoint: Show Active Reserved Items
````java
@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class UserReservationController {

    private final UserReservationRepository reservationRepository;

    @GetMapping("/active")
    public List<UserReservationDto> getActiveReservations(@RequestParam Long userId) {
        List<UserReservation> activeReservations = reservationRepository
            .findByUserIdAndIsSoldFalseAndExpiryAtAfter(userId, LocalDateTime.now());

        return activeReservations.stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    private UserReservationDto toDto(UserReservation reservation) {
        return UserReservationDto.builder()
                .schemaTitle(reservation.getSchema().getTitle())
                .reservedAmount(reservation.getReservedAmount())
                .reservedAt(reservation.getReservedAt())
                .expiryAt(reservation.getExpiryAt())
                .imageUrl(reservation.getSchema().getImageUrl())
                .incomeEarned(reservation.getIncomeEarned())
                .build();
    }
}

````

## ✅ Enforcing "Only One Reservation Per Day"
````java
boolean alreadyReserved = reservationRepository.existsByUserIdAndReservationDate(userId, LocalDate.now());
if (alreadyReserved) {
    throw new IllegalStateException("You can only reserve once per day.");
}
````

## ✅ Scheduled Task to Move Expired Reservations
````java
@Scheduled(cron = "0 0 1 * * *") // 1 AM daily
public void expireReservations() {
    List<UserReservation> expired = reservationRepo.findByIsSoldFalseAndExpiryAtBefore(LocalDateTime.now());
    for (UserReservation reservation : expired) {
        // Move to collection or mark for history
        reservation.setIsSold(false); // Or keep as unsold expired
        // Optionally create UserInvestment for permanent holding
    }
    reservationRepo.saveAll(expired);
}

````

## 🧠 Why This Approach Is Better
| Aspect                | Using `UserInvestment`   | Using Separate `UserReservation`  |
| --------------------- | ------------------------ | --------------------------------- |
| Clarity               | ❌ Overloaded entity      | ✅ Separation of concern           |
| Validation            | ❌ Complex flags          | ✅ Clean logic with DB constraints |
| UI Support            | ❌ Complex filtering      | ✅ Simple `isActive()` check       |
| Future Scalability    | ❌ Hard to extend         | ✅ Easy to manage daily logic      |
| Cron / Archival logic | ❌ Mixed with investments | ✅ Easy to move/expire             |


## ✅ Optional Improvements
- Add ReservationStatus enum: ACTIVE, SOLD, EXPIRED.
- Emit domain events on reservation/sell to trigger income or wallet updates.
- Consider ReservationIncomeLog entity if you track per-payout earnings.