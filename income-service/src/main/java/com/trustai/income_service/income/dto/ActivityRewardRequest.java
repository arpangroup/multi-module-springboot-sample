package com.trustai.income_service.income.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ActivityRewardRequest(
        @NotNull(message = "userId is missing")
        Long userId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be at least 0.01")
        BigDecimal rewardAmount,

        @NotNull(message = "message is required")
        String message
) {
}
