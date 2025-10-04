package com.trustai.common.enums;

import java.util.List;

public enum TransactionType {
    // Deposit & Withdrawal
    DEPOSIT("Deposit"),
    DEPOSIT_MANUAL("Manual Deposit"),
    WITHDRAWAL("Withdrawal"), // from wallet
    WITHDRAWAL_FROM_PROFIT("Withdrawal From Profit"),
    REFUND("Refund"),

    // Adjustments
    ADD("Credit"),
    SUBTRACT("Debit"),      // ADJUSTMENT

    // Transfers
    SEND_MONEY("TRANSFER"), // TRANSFER
    RECEIVE_MONEY("TRANSFER"), // TRANSFER

    // Investments
    INVESTMENT("Investment"),
    INVESTMENT_RESERVE("Reserve"),
    INVESTMENT_MATURITY("Investment Mature"),

    // Currency Operations
    EXCHANGE("Exchange"),

    // Bonuses & Incentives
    SIGNUP_BONUS("Signup"),
    REFERRAL("Referral"),
    BONUS("BONUS"),

    // Earnings
    DAILY_INCOME("Daily Income"),
    TEAM_INCOME("Team Income"),
    INTEREST("Interest");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }


    public static List<TransactionType> getProfitTypes() {
        return List.of(SIGNUP_BONUS, REFERRAL, BONUS, DAILY_INCOME, TEAM_INCOME, INVESTMENT, INVESTMENT_MATURITY, INTEREST);
    }
}
