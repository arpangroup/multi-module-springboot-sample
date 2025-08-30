package com.trustai.income_service.income.dto;

import java.math.BigDecimal;

public record UplineIncomeLog(
        Long uplineUserId,
        String rankCode,
        int depth,
        BigDecimal percentage,
        BigDecimal income
) {
}
