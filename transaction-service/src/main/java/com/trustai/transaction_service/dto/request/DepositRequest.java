package com.trustai.transaction_service.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DepositRequest extends ManualDepositRequest {
    @NotNull(message = "Payment gateway is required")
    @Pattern(
            regexp = "SYSTEM|BINANCE|COINBASE",
            message = "Payment gateway must be one of: SYSTEM, BINANCE, COINBASE"
    )
    private String paymentGateway; // PaymentGateway.valueOf(request.getPaymentGateway())

    @NotNull(message = "txnRefId is required")
    @Size(max = 50, message = "Transaction reference ID must not exceed 50 characters")
    private String txnRefId;

    /*@Size(max = 3, message = "currencyCode must not exceed 3 characters")
    private String currencyCode;*/

    public DepositRequest(Long userId, BigDecimal amount, String paymentGateway, String txnRefId, String remarks, String metaInfo) {
        super(userId, amount, metaInfo, remarks);
        this.paymentGateway = paymentGateway;
        this.txnRefId = txnRefId;
    }
}