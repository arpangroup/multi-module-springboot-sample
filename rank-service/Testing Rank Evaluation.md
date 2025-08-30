## ✅ What to Test in RankEvaluatorService
| Test Scenario                                   | Purpose                                  |
| ----------------------------------------------- | ---------------------------------------- |
| User meets all criteria → correct rank assigned | Basic happy path                         |
| User does not meet any rank criteria            | Should not be upgraded                   |
| User meets criteria for intermediate rank only  | Highest matching rank is selected        |
| Rank downgrade is prevented (if not allowed)    | Ensures rank doesn't fall without policy |
| Rank config is inactive → user shouldn’t match  | Rank filtering works properly            |
| Edge cases (e.g. missing metrics, missing user) | Error handling                           |

## ✅ Example: RankEvaluatorServiceTest