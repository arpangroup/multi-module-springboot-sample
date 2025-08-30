
## 📚 Suggested Structure
````
mlm-rank-service
├── entity
│   └── RankConfig.java
├── evaluation
│   ├── RankSpecification.java
│   ├── DepositAmountSpec.java
│   └── DirectReferralSpec.java
├── service
│   ├── RankEvaluatorService.java
│   └── RankCalculationOrchestrationService.java
├── controller
│   └── RankController.java (optional, if exposing HTTP endpoint)
├── events
│   └── handlers (if subscribing to events like DepositEvent, UserActivatedEvent)
├── config
│   └── RankModuleConfig.java

````

## 🧩 Interactions With Other Services
| Other Service          | How It Interacts With Rank                   |
| ---------------------- | -------------------------------------------- |
| `user-service`         | Can update user’s rankCode based on result   |
| `income-service`       | Can compute commission based on current rank |
| `inAppNotification-service` | Send congratulatory messages on rank up      |
| `hierarchy-service`    | Supplies team structure/depth info           |
| `referral-service`     | Supplies direct referral counts or status    |

## 🔄 Optional: Expose Public Interface
You can expose a public interface via:
- RankEvaluatorClient (Java interface)
- REST endpoint (GET /rank/evaluate/{userId})
- Kafka consumer (UserActivatedEvent, DepositEvent)

## Responsibilities of RankCalculationOrchestrationService
| Concern                         | Description                                                                              |
| ------------------------------- | ---------------------------------------------------------------------------------------- |
| **Trigger rank evaluation**     | Call `RankEvaluatorService.evaluate()` when needed (e.g., deposit, activation, referral) |
| **Update user profile**         | Send result (e.g., `rankCode` or `rankId`) to `user-service`                             |
| **Prevent unnecessary updates** | Skip if user already holds the same or higher rank                                       |
| **Log history / audit**         | Store `UserActivityLog` or `RankHistory` entries                                         |
| **Publish events** (optional)   | e.g., `UserRankUpgradedEvent`                                                            |
| **Scheduling** (optional)       | Recalculate all users' ranks periodically                                                |

````
┌────────────────────────────┐
│  DepositEvent / Activation │
└────────────┬───────────────┘
             │
┌────────────▼────────────┐
│ RankCalculationOrchestrationService │
└────────────┬────────────┘
             │
    ┌────────▼────────┐
    │ RankEvaluatorService │ ← contains all specs & rules
    └────────────────────┘
             │
┌────────────▼──────────────┐
│ Result → Update user rank │ (via REST/gRPC to user-service)
└───────────────────────────┘

````

## ✅ Naming Alternatives
You could also name it:
- RankCalculationCoordinator
- RankUpdateManager
- UserRankManager
- RankEventProcessor (if event-driven only)

## ✅ When You Need This Layer
Use RankCalculationOrchestrationService if:
- You expect rank updates from multiple sources (deposit, referral, activation).
- You want to keep rank rules separate from execution logic.
- You want to publish events or call APIs after rank is assigned.
