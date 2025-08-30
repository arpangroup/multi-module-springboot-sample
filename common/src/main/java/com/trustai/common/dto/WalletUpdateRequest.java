package com.trustai.common.dto;


import com.trustai.common.enums.TransactionType;

import java.math.BigDecimal;

public record WalletUpdateRequest (
        BigDecimal amount,
        TransactionType transactionType,
        boolean isCredit,
        String sourceModule,
        String remarks,
        String metaInfo
) {}