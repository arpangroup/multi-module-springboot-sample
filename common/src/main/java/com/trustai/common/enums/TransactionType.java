package com.trustai.common.enums;

public enum TransactionType {
    // Deposit & Withdrawal
    DEPOSIT("Deposit"),
    DEPOSIT_MANUAL("Manual Deposit"),
    WITHDRAWAL("Withdrawal"),
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
}
