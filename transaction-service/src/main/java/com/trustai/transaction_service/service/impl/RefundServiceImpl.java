package com.trustai.transaction_service.service.impl;

import com.trustai.common.enums.TransactionType;
import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.repository.TransactionRepository;
import com.trustai.transaction_service.service.RefundService;
import com.trustai.transaction_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefundServiceImpl implements RefundService {
    private final TransactionRepository transactionRepository;
    private final WalletService walletService;

    @Override
    public Transaction refund(long userId, @NonNull BigDecimal amount, String originalTxnRef, String reason) {
        BigDecimal updatedBalance = walletService.getWalletBalance(userId).add(amount);
        Transaction txn = new Transaction(userId, amount, TransactionType.REFUND, updatedBalance, true);
        txn.setStatus(Transaction.TransactionStatus.SUCCESS);
        txn.setRemarks("Refund for txnRef: " + originalTxnRef);
        txn.setMetaInfo(reason);;
        transactionRepository.save(txn);
        walletService.updateBalanceFromTransaction(userId, amount);
        return txn;
    }
}
