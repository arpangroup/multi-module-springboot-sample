package com.trustai.transaction_service.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class DepositRequest {
    @NotNull(message = "Payment gateway is required")
    @Pattern(
            regexp = "SYSTEM|BINANCE|COINBASE|BITGET|TRUST_WALLET|SAFEPAL_WALLET",
            message = "Payment gateway must be one of: SYSTEM, BINANCE, COINBASE, BITGET, TRUST_WALLET, SAFEPAL_WALLET"
    )
    private String paymentGateway; // PaymentGateway.valueOf(request.getPaymentGateway())


    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be at least 0.01")
    private BigDecimal amount;

    @NotNull(message = "txnRefId is required")
    @Size(max = 250, message = "Transaction reference ID must not exceed 50 characters")
    private String txnRefId;

    /*@Size(max = 3, message = "currencyCode must not exceed 3 characters")
    private String currencyCode;*/


    @Size(max = 100, message = "Meta info must not exceed 100 characters")
    private String metaInfo;

    @Size(max = 255, message = "Remarks must not exceed 255 characters")
    private String remarks;

    public DepositRequest(BigDecimal amount, String paymentGateway, String txnRefId, String remarks, String metaInfo) {
        this.paymentGateway = paymentGateway;
        this.txnRefId = txnRefId;
    }
}