package com.trustai.common.enums;

public enum IncomeType {
    DAILY,
    TEAM,
    REFERRAL,
    RESERVE, //<---profit= sellAmount - reserved Amount
    ACTIVITY, //<---based on daily activity
    STAKE, //<---after stake mature
}
