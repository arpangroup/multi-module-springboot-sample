package com.trustai.transaction_service.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record WithdrawRequest(
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be at least 0.01")
        BigDecimal amount,

        boolean isWithdrawFromProfit,

        String walletAddress
) {
}
