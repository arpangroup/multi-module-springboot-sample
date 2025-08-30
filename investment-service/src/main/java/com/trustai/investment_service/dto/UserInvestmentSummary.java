package com.trustai.investment_service.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
| Field Name              | Usage / Purpose                                                                 |
|-------------------------|----------------------------------------------------------------------------------|
| `investmentId`          | Unique identifier for the investment; used for actions like cancel, track, etc. |
| `schemaName`            | Name/title of the investment plan; shown in UI and reports.                     |
| `amountRange`           | Schema's allowed investment range (e.g., ₹1,000 – ₹10,000); useful in display.  |
| `investedAmount`        | Actual amount the user invested; used in all return and profit calculations.    |
| `roiType`               | Type of ROI: `FLAT` or `PERCENTAGE`; guides how to interpret ROI value.         |
| `roiValue`              | Actual rate of return per period; could be ₹100 (flat) or 5% (percentage).      |
| `perPeriodProfit`       | Profit earned in one payout cycle; used in payouts and UI.                      |
| `capitalBack`           | Indicates whether principal is returned at end of investment.                   |
| `capitalReturned`       | Shows if capital has already been returned; helps determine current balance.    |
| `currencyCode`          | Currency type (e.g., INR, USD); supports multi-currency logic.                  |
| `totalPeriods`          | Total number of payout cycles defined in the schema.                            |
| `completedPeriods`      | Number of periods already completed.                                            |
| `remainingPeriods`      | Number of periods left = `totalPeriods - completedPeriods`.                     |
| `expectedReturn`        | Total return (profit only) expected over the life of investment.                |
| `receivedReturn`        | Total profit received so far by the user.                                       |
| `profit`                | Net gain = `receivedReturn - investedAmount` (if capital not returned).         |
| `totalEarningPotential` | Expected return plus capital (if capitalBack is true).                          |
| `earlyExitPenalty`      | Penalty charged if investment is cancelled before grace period.                 |
| `nextReturnAmount`      | Amount to be credited in the upcoming payout; typically equals `perPeriodProfit`.|
| `subscribedAt`          | Timestamp when the investment was made.                                         |
| `nextPayoutDate`        | Date when the next profit will be credited.                                     |
| `maturityAt`            | Date when investment will end and possibly return capital.                      |
| `payoutFrequencyLabel`  | Human-friendly label like "Weekly", "Monthly", etc.                              |
| `investmentStatus`      | Current status of investment: ACTIVE, CANCELLED, COMPLETED, etc.                |
| `canCancelNow`          | Whether the user can cancel the investment right now.                           |
| `isWithdrawableNow`     | Whether the return is currently eligible for withdrawal.                        |
| `daysRemaining`         | Days left until the investment matures.                                         |

 */
@Data
@Builder
public class UserInvestmentSummary {
    private Long investmentId;
    private String schemaName;
    private String amountRange;
    private String imageUrl;

    private BigDecimal investedAmount;
    private String roiType;         // FLAT / PERCENTAGE
    private BigDecimal roiValue;     // roiRate → roiValue (optional, for UI clarity)
    private BigDecimal perPeriodProfit;


    private boolean capitalBack;
    private boolean capitalReturned; // runtime info
    private String currencyCode;

    private int totalPeriods;
    private int completedPeriods;
    private int remainingPeriods;

    private BigDecimal expectedReturn;
    private BigDecimal receivedReturn;
    private BigDecimal profit; // = receivedReturn - investedAmount (if capital not returned)= receivedReturn only (if capital returned)
    private BigDecimal totalEarningPotential; // expectedReturn + capital (if capitalBack = true)
    private BigDecimal earlyExitPenalty;
    private BigDecimal nextReturnAmount;

    private LocalDateTime subscribedAt;
    private LocalDateTime nextPayoutDate;
    private LocalDateTime maturityAt;
    private String payoutFrequencyLabel; // "Weekly", "Every 15 days", "Custom"

    private String investmentStatus;
    private boolean canCancelNow; // Add logic to check grace period
    private boolean isWithdrawableNow; // Based on periodic returns
    private int daysRemaining;  // Add via helper for Duration.between(now, maturityAt)
}
