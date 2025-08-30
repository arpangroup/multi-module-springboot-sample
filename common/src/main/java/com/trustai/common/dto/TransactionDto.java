package com.trustai.common.dto;

import com.trustai.common.enums.PaymentGateway;
import com.trustai.common.enums.TransactionType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TransactionDto {
    private Long id;
    private Long userId;
    private Long senderId;
    private BigDecimal amount;
    private BigDecimal balance;
    private String txnRefId;
    private String remarks;
    private String status;
    private BigDecimal txnFee;
    private TransactionType txnType;
    private PaymentGateway gateway;
    private String currencyCode;
    private String linkedTxnId;
    private String sourceModule;
    private boolean isCredit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

}
