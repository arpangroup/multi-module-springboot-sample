package com.trustai.common.event;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class DepositActivityEvent extends UserActivityEvent {
    private final BigDecimal amount;
    private final String depositType;

    public DepositActivityEvent(Object source, Long userId, BigDecimal amount, String depositType) {
        super(source, userId, "DEPOSIT");
        this.amount = amount;
        this.depositType = depositType;
    }
}
