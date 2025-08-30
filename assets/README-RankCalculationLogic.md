# Rank / Level Calculation Logic
In a Multi-Level Marketing (MLM) system, updating a user's level or rank depends on the business logic and the criteria defined for rank progression.

### When Should User Rank Be Updated in an MLM System: On Registration, First Deposit, or Scheduled Evaluation?


##  1. After Downline User Registration:
- **Pros**: 
  - **Immediate feedback:** Users see immediate changes in their rank or level when they successfully recruit new members 
  - **Encourages Recruitment:** motivates user with instant results to improve their rank.
- **Cons**: 
  - **May not reflect True Activity:** A user might recruit many members who do not actively participate or make deposits, leading to inflated rank. 

- **Example:**
  - Rank 2: Requires 5 direct referrals
  - Rank 3: Requires 20 total downline members (any depth)


## 2. After First Deposit / Purchase
- **Pros**:
    - **Ensure Active Participation:** By requiring a deposit, this approach ensures that new recruits are actively participating in the system.
    - **Aligns with Revenue:** Levels or ranks are updated based on actual financial contributions, aligning with the company's revenue goals
- **Cons**:
    - **Delayed Feedback:** Users may not see immediate changes in their rank until the new recruit makes a deposit

- **Example:**
  - Rank 2: 3 direct referrals, each must deposit $100+
  - Rank 3: Total downline deposit volume ≥ $1000

## 3. On a Daily Scheduled Job
- **Pros**:
  - **Consistent Updates:** Regular updates ensure that ranks reflect the most current state of the network
  - **Encourages Recruitment:** motivates user with instant results to improve their rank.
- **Cons**:
  - User doesn’t see instant feedback.


## Recommended Hybrid Approach: 
- **Immediate Update:** Update a user's rank immediately after a downline member makes their first deposit.
- **Scheduled Evaluation:** Perform a daily or weekly evaluation to adjust ranks based on overall activity, including new recruits, deposit and sale.

---

## ✅ Recommended Event Name:
UserRankUpdateEvent

| Name                                   | Use Case                                             |
| -------------------------------------- | ---------------------------------------------------- |
| `UserActivityTriggeredRankUpdateEvent` | When triggered by actions like deposit, registration |
| `ScheduledRankEvaluationEvent`         | For scheduled/daily batch processing                 |
| `ReferralBasedRankUpdateEvent`         | Triggered by new downline registrations              |
| `BusinessVolumeRankUpdateEvent`        | When based on sales or deposits (BV)                 |
| `RankRecalculationEvent`               | Generic, reusable for any rank recalculation trigger |



## ✅ Recommended Class Names:
| Class Name               | When to Use                                                    |
| ------------------------ | -------------------------------------------------------------- |
| `RankCalculationService` | Most common and clear — holds logic for calculating ranks      |
| `UserRankEvaluator`      | Emphasizes evaluation/decision-making aspect                   |
| `RankComputationService` | Focuses on computation-heavy or rules-based logic              |
| `UserRankProcessor`      | Suitable if it's part of a larger processing workflow          |
| `RankEngine`             | Good if it's a rule-driven, pluggable or strategy-based engine |



---

## What design pattern is best suited for determining a user's rank in an MLM system, where each rank has different and configurable eligibility criteria?

In our MLM system, each rank (e.g., Rank1, Rank2, etc.) has its own requirements. These requirements may include factors such as wallet balance thresholds and the number of downline users at different levels (e.g., Level-A, Level-B, Level-C). The system should allow:
- Configurable eligibility criteria for each rank (via config files or database).
- Extensible logic so that new rank evaluation rules can be added without impacting existing ones.
- Flexibility to support complex logic for future ranks.
  
**Example Rank Criteria:**
- Rank1:
  - Wallet balance between 50 and 100
- Rank2:
  - Wallet balance between 500 and 2000
  - At least 3 users in Level-A, 4 in Level-B, and 1 in Level-C
- Rank3:
  - Wallet balance between 2000 and 5000
  - At least 6 users in Level-A, 18 in Level-B, and 2 in Level-C
  - **For Rank3, you need to:**
    - First get Rank1 users.
    - Then from those, get Rank2 users.
    - Finally, their descendants will be Rank3 candidates.
- Rank4:
  - Wallet balance between 5000 and 10000
  - At least 15 users in Level-A, 30 in Level-B, and 5 in Level-C
  - **For Rank3, you need to:**
    - First get Rank1 users.
    - Then from those, get Rank2 users.
    - so on....

What would be the most appropriate design pattern to implement this kind of rank evaluation logic, keeping configurability and future extensibility in mind?
**Repeated DB calls must be avoided (e.g., reuse results for Rank1 while evaluating Rank2, etc.)**



## ✅ Recommended Design Pattern: Strategy Pattern

**1. Rank.java (Enum)**
````java
public enum Rank {
    RANK_1, RANK_2, RANK_3, RANK_4, RANK_5
}
````

**2. RankConfig.java**
````java
@Entity
@Table(name = "rank_config")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RankConfig {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "rank_type") // Avoid reserved keyword
    private Rank rank;

    private int minWalletBalance;
    private int maxWalletBalance;
    private String reservationRange;
    private double profitPerDay;
    private double annualizedReturns;

    @ElementCollection
    @CollectionTable(name = "rank_required_downlines", joinColumns = @JoinColumn(name = "rank"))
    @MapKeyColumn(name = "depth")
    @Column(name = "required_count")
    private Map<Integer, Integer> requiredLevelCounts = new HashMap<>();
}
````

**3. RankEvaluationContext.java**
````java
@Data
@AllArgsConstructor
public class RankEvaluationContext {
    private Long userId;
    private int walletBalance;
    private Map<Integer, List<Long>> downlineMapByLevel; // Level -> List of userIds
}
````


**4. RankEvaluationStrategy.java**
````java
public interface RankEvaluationStrategy {
    boolean isEligible(RankEvaluationContext context);
    Rank getRank();
}
````

**5. Rank2Evaluation.java (example implementation)**
````java
@Component
@RequiredArgsConstructor
public class Rank2Evaluation implements RankEvaluationStrategy {

    private final RankConfigRepository configRepository;

    @Override
    public boolean isEligible(RankEvaluationContext context) {
        RankConfig config = configRepository.findById(Rank.RANK_2).orElse(null);
        if (config == null) return false;

        if (context.getWalletBalance() < config.getMinWalletBalance() || 
            context.getWalletBalance() > config.getMaxWalletBalance()) {
            return false;
        }

        Map<Integer, Integer> required = config.getRequiredLevelCounts();

        for (Map.Entry<Integer, Integer> entry : required.entrySet()) {
            int level = entry.getKey();
            int requiredCount = entry.getValue();

            List<Long> usersAtLevel = context.getDownlineMapByLevel().getOrDefault(level, Collections.emptyList());
            if (usersAtLevel.size() < requiredCount) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Rank getRank() {
        return Rank.RANK_2;
    }
}
````

**6. RankEvaluationService.java**
````java
@Service
@RequiredArgsConstructor
public class RankEvaluationService {

    private final List<RankEvaluationStrategy> strategies;
    private final UserHierarchyService hierarchyService;

    public Rank evaluateUserRank(Long userId, int walletBalance) {
        Map<Integer, List<Long>> downlineByLevel = hierarchyService.getDownlinesGroupedByLevel(userId);
        RankEvaluationContext context = new RankEvaluationContext(userId, walletBalance, downlineByLevel);

        return strategies.stream()
                .filter(strategy -> strategy.isEligible(context))
                .map(RankEvaluationStrategy::getRank)
                .max(Comparator.comparing(Enum::ordinal)) // Choose highest eligible rank
                .orElse(Rank.RANK_1); // Default rank
    }
}
````

**7. UserHierarchyService.java**
````java
@Service
@RequiredArgsConstructor
public class UserHierarchyService {

    private final UserHierarchyRepository repository;

    public Map<Integer, List<Long>> getDownlinesGroupedByLevel(Long userId) {
        List<UserHierarchy> hierarchy = repository.findAllByAncestor(userId);

        return hierarchy.stream()
                .filter(h -> h.isActive() && h.getDepth() >= 1 && h.getDepth() <= 3)
                .collect(Collectors.groupingBy(
                        UserHierarchy::getDepth,
                        Collectors.mapping(UserHierarchy::getDescendant, Collectors.toList())
                ));
    }
}
````









