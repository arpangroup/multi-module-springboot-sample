package com.trustai.transaction_service.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RejectDepositRequest(
        @NotNull(message = "rejectionReason is mandatory")
        @Size(min = 2, max = 100, message = "rejectionReason must be between 2 and 100 characters")
        String rejectionReason
) {}
