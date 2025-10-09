package com.trustai.common.event;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class DepositActivityEvent extends UserActivityEvent {
    private final BigDecimal amount;
    private boolean isFirstDeposit;

    public DepositActivityEvent(Object source, Long userId, BigDecimal amount, boolean isFirstDeposit) {
        super(source, userId, "DEPOSIT");
        this.amount = amount;
        this.isFirstDeposit = isFirstDeposit;
    }
}
