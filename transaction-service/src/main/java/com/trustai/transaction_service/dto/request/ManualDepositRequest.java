package com.trustai.transaction_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ManualDepositRequest {
    @NotNull(message = "userId is required")
    @Positive(message = "userId must be a positive number")
    private Long userId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be at least 0.01")
    private BigDecimal amount;

   /* @NotNull(message = "linkedAccountId is required")
    @Size(max = 20, message = "linkedAccountId must not exceed 20 characters")
    private String linkedAccountId;*/

    @Size(max = 100, message = "Meta info must not exceed 100 characters")
    private String metaInfo;

    @Size(max = 255, message = "Remarks must not exceed 255 characters")
    private String remarks;
}