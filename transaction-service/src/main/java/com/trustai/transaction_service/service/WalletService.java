package com.trustai.transaction_service.service;

import com.trustai.common.dto.WalletResponse;
import com.trustai.common.enums.TransactionType;
import com.trustai.transaction_service.entity.Transaction;

import java.math.BigDecimal;

public interface WalletService {
    WalletResponse getWalletBalance(Long userId);

    void updateBalanceFromTransaction(Long userId, BigDecimal delta, boolean isProfitWallet);

    void ensureSufficientBalance(Long userId, BigDecimal amount, boolean isProfitWallet);

    @Deprecated
    Transaction updateWalletBalance(Long userId, BigDecimal amount, TransactionType transactionType, String sourceModule, boolean isCredit, String remarks, String metaInfo, Boolean isProfitWallet);

    Transaction updateWalletBalance(Long userId, BigDecimal amount, BigDecimal txnFee, TransactionType transactionType, String sourceModule, boolean isCredit, String remarks, String metaInfo, Boolean isProfitWallet);
}
