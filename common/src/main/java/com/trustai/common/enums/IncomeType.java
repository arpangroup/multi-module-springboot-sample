package com.trustai.common.enums;

public enum IncomeType {
    SIGNUP_BONUS,
    DAILY,
    TEAM,
    REFERRAL,
    RESERVE, //<---profit= sellAmount - reserved Amount
    ACTIVITY, //<---based on daily activity
    STAKE, //<---after stake mature
}
