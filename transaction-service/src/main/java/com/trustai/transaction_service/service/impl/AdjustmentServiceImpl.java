package com.trustai.transaction_service.service.impl;

import com.trustai.common.enums.PaymentGateway;
import com.trustai.common.enums.TransactionType;
import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.repository.TransactionRepository;
import com.trustai.transaction_service.service.AdjustmentService;
import com.trustai.transaction_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdjustmentServiceImpl implements AdjustmentService {
    private final TransactionRepository transactionRepository;
    private final WalletService walletService;

    @Override
    public Transaction addBalance(long userId, BigDecimal amount, String reason) {
        log.debug("Attempting to add balance. userId={}, amount={}, reason={}", userId, amount, reason);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid amount: {}. Must be greater than zero.", amount);
            throw new IllegalArgumentException("Amount to add must be greater than zero.");
        }

        BigDecimal currentBalance = walletService.getWalletBalance(userId);
        BigDecimal newBalance = currentBalance.add(amount);
        log.debug("Current balance={}, New balance after add={}", currentBalance, newBalance);

        Transaction txn = new Transaction(userId, amount, TransactionType.ADD, newBalance, true);
        txn.setGateway(PaymentGateway.SYSTEM);
        txn.setStatus(Transaction.TransactionStatus.SUCCESS);
        txn.setRemarks("Manual Add: " + reason);
        txn.setMetaInfo("manual_addition");

        transactionRepository.save(txn);
        log.info("Transaction saved successfully. txnId={}", txn.getId());

        walletService.updateBalanceFromTransaction(userId, amount);
        log.info("Balance updated in wallet for userId={}", userId);

        return txn;
    }

    @Override
    public Transaction subtractBalance(long userId, BigDecimal amount, String reason) {
        log.debug("Attempting to subtract balance. userId={}, amount={}, reason={}", userId, amount, reason);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid amount: {}. Must be greater than zero.", amount);
            throw new IllegalArgumentException("Amount to subtract must be greater than zero.");
        }

        walletService.ensureSufficientBalance(userId, amount);
        log.debug("Sufficient balance confirmed for userId={}", userId);

        BigDecimal currentBalance = walletService.getWalletBalance(userId);
        BigDecimal newBalance = currentBalance.subtract(amount);
        log.debug("Current balance={}, New balance after subtract={}", currentBalance, newBalance);

        Transaction txn = new Transaction(userId, amount, TransactionType.SUBTRACT, newBalance, false);
        txn.setGateway(PaymentGateway.SYSTEM);
        txn.setStatus(Transaction.TransactionStatus.SUCCESS);
        txn.setRemarks("Adjustment: " + reason);
        txn.setMetaInfo("manual_adjustment");

        transactionRepository.save(txn);
        log.info("Transaction saved successfully. txnId={}", txn.getId());

        walletService.updateBalanceFromTransaction(userId, amount.negate());
        log.info("Balance updated in wallet for userId={}", userId);

        return txn;
    }

}
