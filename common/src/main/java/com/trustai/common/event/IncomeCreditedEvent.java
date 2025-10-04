package com.trustai.common.event;

import com.trustai.common.enums.IncomeType;

import java.math.BigDecimal;

public record IncomeCreditedEvent(
    Long userId,
    BigDecimal amount,
    IncomeType incomeType,
    String note
){}
