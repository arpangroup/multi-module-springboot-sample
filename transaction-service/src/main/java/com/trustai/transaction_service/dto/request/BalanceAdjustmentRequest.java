package com.trustai.transaction_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class BalanceAdjustmentRequest {
    private long userId;

    @Min(value = 1, message = "Amount must be at least 1")
    private BigDecimal amount;

    @NotBlank(message = "Remarks cannot be empty")
    private String remarks;

    public enum AdjustmentType {
        ADD,
        SUBTRACT
    }
}
