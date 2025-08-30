package com.trustai.transaction_service.service.impl;

import com.trustai.common.enums.TransactionType;
import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.repository.TransactionRepository;
import com.trustai.transaction_service.service.InvestmentTransactionService;
import com.trustai.transaction_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvestmentTransactionServiceImpl implements InvestmentTransactionService {
    private final TransactionRepository transactionRepository;
    private final WalletService walletService;

    @Override
    public Transaction invest(long userId, BigDecimal amount, String investmentType, String metaInfo) {
        walletService.ensureSufficientBalance(userId, amount);
        BigDecimal updatedBalance = walletService.getWalletBalance(userId).subtract(amount);
        Transaction txn = new Transaction(userId, amount, TransactionType.INVESTMENT, updatedBalance, false); // isCredit = false, because amount is deducting from wallet and investing in stake
        txn.setStatus(Transaction.TransactionStatus.SUCCESS);
        txn.setRemarks("Investment in: " + investmentType);
        txn.setMetaInfo(metaInfo);
        transactionRepository.save(txn);
        walletService.updateBalanceFromTransaction(userId, amount.negate());
        return txn;
    }
}
