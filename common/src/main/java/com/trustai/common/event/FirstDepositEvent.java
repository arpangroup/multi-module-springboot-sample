package com.trustai.common.event;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class FirstDepositEvent {
    private final Long userId;
    private final BigDecimal amount;

    public FirstDepositEvent(Long userId, BigDecimal amount) {
        this.userId = userId;
        this.amount = amount;
    }
}
