package com.trustai.income_service.referral.entity;

public enum BonusStatus {
    PENDING,    // Waiting for evaluation
    APPROVED,   // Successfully granted
    REJECTED    // 	Evaluation done, but not qualified
}
