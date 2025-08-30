package com.trustai.common.event;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class FirstDepositEvent {
    private final Long userId;
    private final Long referrerId;
    private final BigDecimal amount;

    public FirstDepositEvent(Long userId, Long referrerId, BigDecimal amount) {
        this.userId = userId;
        this.referrerId = referrerId;
        this.amount = amount;
    }
}
