package com.trustai.investment_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class InvestmentRequest {
    @NotNull(message = "planId is required")
    @Positive(message = "planId schemaId")
    private Long schemaId;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "amount must be at least 0.01")
    private BigDecimal amount;
}
