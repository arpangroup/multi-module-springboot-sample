package com.trustai.transaction_service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DepositHistoryItem {
    private Long id;
    private String txnRefId;
    private BigDecimal amount;
    private String linkedAccountId;
    private String paymentGateway;
    private BigDecimal txnFee;
    private String currencyCode;
    private String status;
    private String remarks;
    private String txnDate;
}
