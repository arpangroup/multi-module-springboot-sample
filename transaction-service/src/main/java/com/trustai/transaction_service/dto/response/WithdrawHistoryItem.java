package com.trustai.transaction_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawHistoryItem {
    private Long id;
    private String txnRefId;
    private BigDecimal amount;
    private BigDecimal txnFee;
    private String status;
    private String txnDate;
}
