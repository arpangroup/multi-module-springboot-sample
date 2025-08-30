## Investment Schema

| Field Name                       | Description                                                       |
| -------------------------------- | ----------------------------------------------------------------- |
| `id`                             | Primary key identifier.                                           |
| `title`                          | Descriptive title of the schema (e.g., “Gold Saver 2025”).        |
| `schemaType`                     | Indicates whether it uses fixed terms or varies by amount/period. |
| `minimumInvestmentAmount`        | Lowest allowed investment amount.                                 |
| `maximumInvestmentAmount`        | Maximum allowable investment.                                     |
| `returnRate`                     | Interest or profit rate for the investment.                       |
| `interestCalculationMethod`      | Whether the return is flat or percentage-based.                   |
| `returnSchedule`                 | Period/frequency when returns are disbursed.                      |
| `returnType`                     | Describes whether returns are recurring or a one-time payment.    |
| `totalReturnPeriods`             | Total number of return intervals (used if periodic).              |
| `isCapitalReturned`              | If principal amount is returned after maturity.                   |
| `isFeatured`                     | Used for highlighting in UI/promotions.                           |
| `isCancellable`                  | If investor can cancel the schema.                                |
| `cancellationGracePeriodMinutes` | How long after activation the cancellation is allowed.            |
| `isTradeable`                    | Whether schema participation is transferable.                     |
| `isActive`                       | Whether this schema is currently available.                       |
| `description`                    | Optional, human-readable summary.                                 |
| `createdAt / updatedAt`          | Auditing timestamps.                                              |
| `createdBy / updatedBy`          | Optional user/account tracking.                                   |
| `currency`                       | Useful for global/multi-region investment schemas.                |
| `earlyExitPenalty`               | Applicable if schema ends before full maturity.                   |
| `termsAndConditionsUrl`          | Optional link to external terms.                                  |
