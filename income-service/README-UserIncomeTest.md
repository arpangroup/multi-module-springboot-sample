
## 🔍 Test Inputs:
Mocked repository return from `incomeRepo.findIncomeSummaryByUserId(userId, ...)`:

| IncomeType | DailyIncome | TotalIncome |
| ---------- | ----------- | ----------- |
| DAILY      | 5.00        | 50.00       |
| DAILY      | 7.50        | 60.00       |
| TEAM       | 2.00        | 30.00       |


## ⚙️ Expected Behavior:
1. Aggregation by Type:
   - DAILY:
     - Daily Total = 5.00 + 7.50 = 12.50
     - Overall Total = 50.00 + 60.00 = 110.00
   - TEAM:
     - Daily = 2.00
     - Total = 30.00
2. Mapping Labels:
   - DAILY → "Comprehensive"
   - TEAM → "Team"
3. Appending Reserve:
   - Type: "Reserve"
   - Daily = 1.30
   - Total = 1.30 (hardcoded)
4. Final Result List (in order):
   - "Comprehensive" → 12.50 daily, 110.00 total
   - "Reserve" → 1.30 daily, 1.30 total
   - "Team" → 2.00 daily, 30.00 total


---

## ✅ Test Scenario: Validate Aggregated User Income Summary Including Reserve Entry
### 🎯 Objective:
To validate that the system correctly calculates the user’s income summary by aggregating data by income type, converting income types to human-readable labels, and appending a default “Reserve” income row.

## 📥 Test Data Setup in DB (for userId: 1001):
| ID | User ID | Income Type | Daily Income | Total Income |
| -- | ------- | ----------- | ------------ | ------------ |
| 1  | 1001    | DAILY       | 5.00         | 50.00        |
| 2  | 1001    | DAILY       | 7.50         | 60.00        |
| 3  | 1001    | TEAM        | 2.00         | 30.00        |
| 4  | 1001    | BONUS       | 1.00         | 10.00        |

## ⚙️ Expected Behavior:
When `getUserIncomeSummary(1001)` is called, the system should:
1. Aggregate values by income type:
   - DAILY → Daily: 12.50, Total: 110.00
   - TEAM → Daily: 2.00, Total: 30.00
   - BONUS → Daily: 1.00, Total: 10.00
2. Convert type to label:
   - DAILY → "Comprehensive"
   - TEAM → "Team"
   - BONUS → "Bonus"
3. Append "Reserve" entry with static values:
   - Reserve → Daily: 1.30, Total: 1.30
4. Final result list:
   - (Assuming order is: Comprehensive, Reserve, Team, Bonus)

| Type          | Daily Income | Total Income |
| ------------- | ------------ | ------------ |
| Comprehensive | 12.50        | 110.00       |
| Reserve       | 1.30         | 1.30         |
| Team          | 2.00         | 30.00        |
| Bonus         | 1.00         | 10.00        |
