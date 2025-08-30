package com.trustai.transaction_service.service.impl;

import com.trustai.common.enums.TransactionType;
import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.repository.TransactionRepository;
import com.trustai.transaction_service.service.ExchangeService;
import com.trustai.transaction_service.service.WalletService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeServiceImpl implements ExchangeService {
    private final TransactionRepository transactionRepository;
    private final WalletService walletService;

    @Override
    @Transactional
    public Transaction exchange(long userId, BigDecimal fromAmount, String fromCurrency, BigDecimal toAmount, String toCurrency, String metaInfo) {
        walletService.ensureSufficientBalance(userId, fromAmount);
        BigDecimal currentBalance = walletService.getWalletBalance(userId);
        BigDecimal updatedBalance = currentBalance.subtract(fromAmount).add(toAmount);
        Transaction txn = new Transaction(userId, toAmount, TransactionType.EXCHANGE, updatedBalance, true);
        txn.setStatus(Transaction.TransactionStatus.SUCCESS);
        txn.setRemarks("Exchange: " + fromCurrency + " to " + toCurrency);
        txn.setMetaInfo(metaInfo);
        transactionRepository.save(txn);
        walletService.updateBalanceFromTransaction(userId, toAmount.subtract(fromAmount));
        return txn;
    }
}
