package com.trustai.common.dto;


import com.trustai.common.enums.TransactionType;

import java.math.BigDecimal;

public record WalletUpdateRequest (
        BigDecimal amount,
        TransactionType transactionType,
        boolean isCredit,
        String sourceModule,
        String remarks,
        String metaInfo,
        Boolean isProfitWallet
) {
    // Existing constructor (auto-generated) will require all 7 params

    // Add a secondary constructor for backward compatibility
    public WalletUpdateRequest(
            BigDecimal amount,
            TransactionType transactionType,
            boolean isCredit,
            String sourceModule,
            String remarks,
            String metaInfo
    ) {
        this(amount, transactionType, isCredit, sourceModule, remarks, metaInfo, false); // default: false
    }
}