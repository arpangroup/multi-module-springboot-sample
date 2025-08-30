package com.trustai.transaction_service.service.impl;

import com.trustai.common.enums.PaymentGateway;
import com.trustai.common.enums.TransactionType;
import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.repository.TransactionRepository;
import com.trustai.transaction_service.service.BonusTransactionService;
import com.trustai.transaction_service.service.WalletService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class BonusTransactionServiceImpl implements BonusTransactionService {
    private final TransactionRepository transactionRepository;
    private final WalletService walletService;

    @Override
    @Transactional
    public Transaction applySignupBonus(long userId, BigDecimal bonusAmount) {
        BigDecimal updatedBalance = walletService.getWalletBalance(userId).add(bonusAmount);
        Transaction txn = new Transaction(userId, bonusAmount, TransactionType.BONUS, updatedBalance, true);
        txn.setGateway(PaymentGateway.SYSTEM);
        txn.setStatus(Transaction.TransactionStatus.SUCCESS);
        txn.setRemarks("Signup Bonus");
        txn.setMetaInfo("signup_bonus");
        transactionRepository.save(txn);
        walletService.updateBalanceFromTransaction(userId, bonusAmount);
        return txn;
    }

    @Override
    @Transactional
    public Transaction applyReferralBonus(long referrerUserId, long referredUserId, BigDecimal bonusAmount) {
        BigDecimal updatedBalance = walletService.getWalletBalance(referrerUserId).add(bonusAmount);
        Transaction txn = new Transaction(referrerUserId, bonusAmount, TransactionType.BONUS, updatedBalance, true);
        txn.setGateway(PaymentGateway.SYSTEM);
        txn.setStatus(Transaction.TransactionStatus.SUCCESS);
        txn.setRemarks("Referral Bonus for referring user: " + referredUserId);
        txn.setMetaInfo("referral_bonus");
        transactionRepository.save(txn);
        walletService.updateBalanceFromTransaction(referrerUserId, bonusAmount);
        return txn;
    }

    @Override
    @Transactional
    public Transaction applyBonus(long userId, BigDecimal bonusAmount, String reason) {
        BigDecimal updatedBalance = walletService.getWalletBalance(userId).add(bonusAmount);
        Transaction txn = new Transaction(userId, bonusAmount, TransactionType.BONUS, updatedBalance, true);
        txn.setGateway(PaymentGateway.SYSTEM);
        txn.setStatus(Transaction.TransactionStatus.SUCCESS);
        txn.setRemarks("Bonus: " + reason);
        txn.setMetaInfo("custom_bonus");
        transactionRepository.save(txn);
        walletService.updateBalanceFromTransaction(userId, bonusAmount);
        return txn;
    }

    @Override
    @Transactional
    public Transaction applyInterest(long userId, BigDecimal interestAmount, String periodDescription) {
        BigDecimal currentBalance = walletService.getWalletBalance(userId);
        BigDecimal updatedBalance = currentBalance.add(interestAmount);

        Transaction txn = new Transaction(userId, interestAmount, TransactionType.INTEREST, updatedBalance, true);
        txn.setGateway(PaymentGateway.SYSTEM);
        txn.setStatus(Transaction.TransactionStatus.SUCCESS);
        txn.setRemarks("Interest for Investment profit for period: " + periodDescription);
        txn.setMetaInfo("interest_payment");

        transactionRepository.save(txn);
        walletService.updateBalanceFromTransaction(userId, interestAmount);
        return txn;
    }
}
