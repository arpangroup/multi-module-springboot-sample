package com.trustai.transaction_service.service.impl;

import com.trustai.common.enums.PaymentGateway;
import com.trustai.common.enums.TransactionType;
import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.repository.TransactionRepository;
import com.trustai.transaction_service.service.WalletService;
import com.trustai.transaction_service.service.WithdrawalService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class WithdrawalServiceImpl implements WithdrawalService {
    private final TransactionRepository transactionRepository;
    private final WalletService walletService;

    @Override
    @Transactional
    public Transaction withdraw(long userId, BigDecimal amount, String destinationAccount, String remarks) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
        }

        walletService.ensureSufficientBalance(userId, amount);

        BigDecimal currentBalance = walletService.getWalletBalance(userId);
        BigDecimal newBalance = currentBalance.subtract(amount);

        Transaction transaction = new Transaction(userId, amount, TransactionType.WITHDRAWAL, newBalance, false);
        transaction.setGateway(PaymentGateway.SYSTEM);
        transaction.setStatus(Transaction.TransactionStatus.SUCCESS);
        transaction.setRemarks(remarks != null ? remarks : "Withdrawal to: " + destinationAccount);
        transaction.setMetaInfo(destinationAccount);

        transactionRepository.save(transaction);
        walletService.updateBalanceFromTransaction(userId, amount.negate());

        return transaction;
    }
}
