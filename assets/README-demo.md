## Income Stream:
1. **Direct Bonus**: - Earn from Referrals `Direct Bonus = Referral Bonus`
  - You earn a commission when you directly refer someone to the platform or program.
  - ➤ Example: You refer Alice, and she signs up — you earn a direct bonus.
2. **Team Bonus**:
   - You earn from the activity or earnings of your referral network (downline), including indirect referrals.
   -  ➤ Example: Alice refers Bob — you earn a bonus based on Bob's actions too.
3. **Daily NFT returns** - Passive Income
  - Earnings generated daily from owning NFTs, which may represent yield-generating digital assets or profit-sharing rights.
  - ➤ Example: You buy an NFT that pays you 1% daily for 100 days.
  - This returns are typically earned without active participation, based on the value or utility of the NFTs
  - Participant earns daily NFT returns by holding or stacking NFTs that generate income or rewards. 
4. **NFT Start Salary** - Monthly  Bonus upto $3,500


## Withdraw:
- Withdrawal Fee: 5% - 8%
- Processing: 1-96 hrs

---

## Suggested Naming:

- DailyTeamIncomeStrategy
- UserLevelIncomeStrategy (for level-based strategy)
- TeamIncomeCalculationService
- DailyIncomeComputationEngine

````java
public interface TeamIncomeCalculationStrategy {
    BigDecimal calculateIncome(Team team);
}
````
````java
public interface DailyTeamIncomeCalculatorStrategy {
    BigDecimal calculateIncome(User user, LocalDate date);
}

public class LevelBasedIncomeCalculator implements DailyTeamIncomeCalculatorStrategy {
    @Override
    public BigDecimal calculateIncome(User user, LocalDate date) {
        // Logic based on user level (A, B, C)
    }
}

public class DailyTeamIncomeService {
    private final List<DailyTeamIncomeCalculatorStrategy> strategies;

    public BigDecimal calculate(User user, LocalDate date) {
        // select and apply appropriate strategy
    }
}
````

````java
import org.apache.catalina.User;

import java.util.List;

public class LevelBasedIncomeStrategy implements IncomeCalculationStrategy {
    @Override
    public double calculateIncome(Team team) {
        double totalIncome = 0.0;
        /*for (User user : team.getUsers()) {            
            switch (user.getLevel) {
                case "Level-A":
                    totalIncome += user.getBaseIncome() * 1.2;
                    break;
                case "Level-B":
                    totalIncome += user.getBaseIncome() * 1.1;
                    break;
                case "Level-C":
                    totalIncome += user.getBaseIncome();
                    break;
            }
        }*/
       for (User user : team.getUsers()) {
           double percentage = getPercentage(user.getLevel(), user.getCategory());
           totalIncome += user.getBaseIncome() * (1 + percentage / 100);
       }
        return totalIncome;
    }
}

public double getPercentage(String level, String category) {
    switch (category) {
       case "Lv.A":
           switch (level) {
              case "Level-2": return 12;
              case "Level-3": return 13;
              case "Level-4": return 15;
              case "Level-5": return 16;
           }
           break;
       case "Lv.B":
          switch (level) {
             case "Level-2": return 5;
             case "Level-3": return 6;
             case "Level-4": return 7;
             case "Level-5": return 8;
          }
          break;
       case "Level-C":
          switch (level) {
             case "Level-2": return 2;
             case "Level-3": return 3;
             case "Level-4": return 5;
             case "Level-5": return 7;
    }
    return 0;
}

public static void main(String[] args) {
        User user1 = new User("Level-3", "Lv.A", 1000);
        User user2 = new User("Level-4", "Lv.B", 1500);
        User user3 = new User("Level-5", "Lv.C", 2000);
        
        Team team = new Team(List.of(user1, user2, user3));
        DailyIncomeCalculator calculator = new DailyTeamIncomeCalculator(new LevelBasedIncomeStrategy());
        
        double totalIncome = calculator.calculate(team);
        System.out.println("Total Daily Team Income: " + totalIncome);
   }
}

@Data
public class Team {
    private List<User> userd;
}

public class User{
    private String level; // Level-1, Level-3, etc..
    private String category; // Lv.A, Lv.B, Lv.C
    private double baseIncome;
}
````

---

````java
@requiredArgsConstructor
public class UplineIncomeStrategy implements IncomeCalculationStrategy {
    private final String uplineLevel;

    @Override
    public double calculateIncome(Team team) {
        double totalIncome = 0.0;
        for (User user : team.getUsers()) {
            double percentage = getPercentage(uplineLevel, user.getCategory());
            totalIncome += user.getBaseIncome() * (percentage / 100);
        }
        return totalIncome;
    }

    public double getPercentage(String uplineLevel, String category) {
        switch (uplineLevel) {
            case "Level-2":
                switch (category) {
                    case "Lv.A":
                        return 12;
                    case "Lv.B":
                        return 6;
                    case "Lv.C":
                        return 3;
                }
                break;
            case "Level-3":
                switch (category) {
                    case "Lv.A":
                        return 13;
                    case "Lv.B":
                        return 7;
                    case "Lv.C":
                        return 3;
                }
                break;
            case "Level-4":
                switch (category) {
                    case "Lv.A":
                        return 15;
                    case "Lv.B":
                        return 7;
                    case "Lv.C":
                        return 5;
                }
                break;
            case "Level-5":
                switch (category) {
                    case "Lv.A":
                        return 16;
                    case "Lv.B":
                        return 8;
                    case "Lv.C":
                        return 7;
                }
                break;
            default:
                return 0; // No team income for Level-1 or invalid level
        }
        return 0;
    }
}

````

---

### FINAL:

### ✅ Step 1: Define the Strategy Interface
````java
public interface DailyTeamIncomeCalculatorStrategy {
   BigDecimal calculateIncome(User upline, User downline, BigDecimal downlineIncome);
}
````

### ✅ Step 2: Create a Level-Based Strategy Implementation
````java
public class LevelBasedIncomeCalculator implements DailyTeamIncomeCalculatorStrategy {

    private static final Map<String, Map<String, BigDecimal>> incomePercentageMap = Map.of(
        "Level-2", Map.of(
            "Lv.A", new BigDecimal("0.12"),
            "Lv.B", new BigDecimal("0.05"),
            "Lv.C", new BigDecimal("0.02")
        ),
        "Level-3", Map.of(
            "Lv.A", new BigDecimal("0.13"),
            "Lv.B", new BigDecimal("0.06"),
            "Lv.C", new BigDecimal("0.03")
        ),
        "Level-4", Map.of(
            "Lv.A", new BigDecimal("0.15"),
            "Lv.B", new BigDecimal("0.07"),
            "Lv.C", new BigDecimal("0.03")
        ),
        "Level-5", Map.of(
            "Lv.A", new BigDecimal("0.16"),
            "Lv.B", new BigDecimal("0.08"),
            "Lv.C", new BigDecimal("0.07")
        )
    );

    @Override
    public BigDecimal calculateIncome(User upline, User downline, BigDecimal downlineIncome) {
        String uplineLevel = upline.getLevel(); // e.g., "Level-2"
        String downlineTier = determineTier(downline); // e.g., "Lv.A"

        Map<String, BigDecimal> tierPercentages = incomePercentageMap.getOrDefault(uplineLevel, Map.of());
        BigDecimal percentage = tierPercentages.getOrDefault(downlineTier, BigDecimal.ZERO);

        return downlineIncome.multiply(percentage);
    }

    private String determineTier(User downline) {
        // Replace with real tier logic, example:
        return downline.getTier(); // returns "Lv.A", "Lv.B", or "Lv.C"
    }
}
````

### ✅ Step 3: Example Usage
````java
User upline = new User("Level-3");
User downline = new User("Lv.B");
BigDecimal downlineIncome = new BigDecimal("100.00");

DailyTeamIncomeCalculatorStrategy strategy = new LevelBasedIncomeCalculator();
BigDecimal teamIncome = strategy.calculateIncome(upline, downline, downlineIncome);

// Output: 6.00

````

---

## Implement Using Database

✅ Recommended Table: `team_income_rate`

| id  | upline\_level | downline\_tier | percentage |
| --- | ------------- | -------------- | ---------- |
| 1   | Level-2       | Lv.A           | 0.12       |
| 2   | Level-2       | Lv.B           | 0.05       |
| 3   | Level-2       | Lv.C           | 0.02       |
| 4   | Level-3       | Lv.A           | 0.13       |
| ... | ...           | ...            | ...        |

✅ Entity Class
````java
@Entity
@Table(name = "team_income_rate")
public class TeamIncomeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "upline_level")
    private String uplineLevel; // e.g., "Level-2"

    @Column(name = "downline_tier")
    private String downlineTier; // e.g., "Lv.A"

    @Column(name = "percentage", precision = 5, scale = 4)
    private BigDecimal percentage; // e.g., 0.12

    // getters/setters
}
````

✅ Insert SQL Statements
````sql
INSERT INTO team_income_rate (upline_level, downline_tier, percentage) VALUES
('Level-2', 'Lv.A', 0.12),
('Level-2', 'Lv.B', 0.05),
('Level-2', 'Lv.C', 0.02),
('Level-3', 'Lv.A', 0.13),
('Level-3', 'Lv.B', 0.06),
('Level-3', 'Lv.C', 0.03),
('Level-4', 'Lv.A', 0.15),
('Level-4', 'Lv.B', 0.07),
('Level-4', 'Lv.C', 0.03),
('Level-5', 'Lv.A', 0.16),
('Level-5', 'Lv.B', 0.08),
('Level-5', 'Lv.C', 0.07);

````

✅ Repository
````java
public interface TeamIncomeRateRepository extends JpaRepository<TeamIncomeRate, Long> {
    Optional<TeamIncomeRate> findByUplineLevelAndDownlineTier(String uplineLevel, String downlineTier);
}
````

✅ Usage in Strategy
````java
public class DatabaseBackedIncomeCalculator implements DailyTeamIncomeCalculatorStrategy {

   private final TeamIncomeRateRepository rateRepo;

   public DatabaseBackedIncomeCalculator(TeamIncomeRateRepository rateRepo) {
      this.rateRepo = rateRepo;
   }

   @Override
   public BigDecimal calculateIncome(User upline, User downline, BigDecimal downlineIncome) {
      String level = upline.getLevel(); // e.g., "Level-3"
      String tier = downline.getTier(); // e.g., "Lv.B"

      BigDecimal percent = rateRepo.findByUplineLevelAndDownlineTier(level, tier)
              .map(TeamIncomeRate::getPercentage)
              .orElse(BigDecimal.ZERO);

      return downlineIncome.multiply(percent);
   }
}

````

````sql
ALTER TABLE team_income_rate
ADD CONSTRAINT chk_upline_level
CHECK (upline_level IN ('Level1', 'Level2'));
````