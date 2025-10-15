package com.trustai.transaction_service.service.impl;

import com.trustai.common.api.UserApi;
import com.trustai.common.dto.UserInfo;
import com.trustai.common.dto.WalletResponse;
import com.trustai.common.enums.CurrencyType;
import com.trustai.common.enums.PaymentGateway;
import com.trustai.common.enums.TransactionType;
import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.exception.InsufficientBalanceException;
import com.trustai.transaction_service.repository.TransactionRepository;
import com.trustai.transaction_service.service.WalletService;
import com.trustai.transaction_service.util.TransactionIdGenerator;
import com.trustai.transaction_service.util.TransactionRemarks;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.trustai.common.enums.TransactionType.INVESTMENT;
import static com.trustai.common.enums.TransactionType.INVESTMENT_MATURITY;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {
    private final TransactionRepository transactionRepository;
    private final UserApi userClient;

    /**
     * Returns the current wallet balance by summing all deposits and bonuses,
     * then subtracting all withdrawals, investments, and transfers sent.
     */
    @Override
    public WalletResponse getWalletBalance(Long userId) {
        log.debug("Retrieving wallet balance for userId: {}", userId);
        /*BigDecimal credits = transactionRepository.sumCredits(userId);
        BigDecimal debits = transactionRepository.sumDebits(userId);
        return credits.subtract(debits);*/
        UserInfo userInfo = userClient.getUserById(userId);
        if (userInfo == null) throw new IllegalArgumentException("User not found");
        return new WalletResponse(userInfo.getWalletBalance(), userInfo.getProfitWallet(), CurrencyType.USD.getSymbol());
    }

    @Override
    @Transactional
    public void updateBalanceFromTransaction(Long userId, BigDecimal delta, boolean isProfitWallet) {
        log.debug("Updating wallet balance for userId: {} with delta: {} with isProfitWallet: {}", userId, delta, isProfitWallet);
        WalletResponse walletResponse = getWalletBalance(userId);
        BigDecimal current = isProfitWallet ? walletResponse.profitWallet() : walletResponse.walletBalance();
        BigDecimal updated = current.add(delta);
        userClient.updateWalletBalance(userId, updated, isProfitWallet);
        log.info("Wallet balance updated for userId: {}. Old Balance: {}, New Balance: {}", userId, current, updated);
    }


    @Override
    public void ensureSufficientBalance(Long userId, BigDecimal amount, boolean isProfitWallet) {
        WalletResponse walletResponse = getWalletBalance(userId);
        BigDecimal current = isProfitWallet ? walletResponse.profitWallet() : walletResponse.walletBalance();
        log.debug("Checking if userId: {} has sufficient balance. Required: {}, Current: {}", userId, amount, current);
        if (current.compareTo(amount) < 0) {
            log.warn("Insufficient balance for userId: {}. Required: {}, Current: {}", userId, amount, current);
            throw new InsufficientBalanceException("Insufficient wallet balance");
        }
    }

    @Deprecated
    @Override
    @Transactional
    public Transaction updateWalletBalance(Long userId, BigDecimal amount, TransactionType transactionType, String sourceModule, boolean isCredit, String remarks, String metaInfo, Boolean isProfitWallet) {
        return this.updateWalletBalance(userId, amount, BigDecimal.ZERO, transactionType, sourceModule, isCredit, remarks, metaInfo, isProfitWallet);
    }

    @Override
    public Transaction updateWalletBalance(Long userId, BigDecimal amount, BigDecimal txnFee, TransactionType transactionType, String sourceModule, boolean isCredit, String remarks, String metaInfo, Boolean isProfitWallet) {
        log.info("Starting wallet transaction [{}] for userId: {}, amount: {}, type: {}, remarks: {}, source: {}",
                isCredit ? "CREDIT" : "DEBIT", userId, amount, transactionType, remarks, sourceModule);


        if (isProfitWallet == null) {
            isProfitWallet = TransactionType.getProfitTypes().contains(transactionType);

            if ((INVESTMENT == transactionType || INVESTMENT_MATURITY == transactionType) && amount.equals(new BigDecimal("15"))) {
                isProfitWallet = false; // for any 15$ investment
            }
        }


        // Load current balance
        WalletResponse walletResponse = getWalletBalance(userId);
        BigDecimal currentBalance = isProfitWallet ? walletResponse.profitWallet() : walletResponse.walletBalance();
        BigDecimal newBalance = currentBalance.add(amount);


        if (!isCredit) {
            ensureSufficientBalance(userId, amount, isProfitWallet);
        }

        Transaction.TransactionStatus status =  Transaction.TransactionStatus.SUCCESS;
        if (TransactionRemarks.WITHDRAW_REQUESTED.equals(remarks)) {
            status = Transaction.TransactionStatus.PENDING;
            remarks = "Withdraw Pending";
        }

        // Create Transaction
        Transaction txn = new Transaction(userId, amount, transactionType, newBalance, isCredit);
        txn.setStatus(status);
        txn.setTxnFee(txnFee);
        txn.setRemarks(remarks);
        txn.setSourceModule(sourceModule);
        txn.setGateway(PaymentGateway.SYSTEM);
        txn.setMetaInfo(metaInfo);

        if (isCredit) {
            txn.setTxnRefId(TransactionIdGenerator.generateTransactionId());
        }

        transactionRepository.save(txn);
        updateBalanceFromTransaction(userId, isCredit ? amount : amount.negate(), isProfitWallet); // Update wallet balance

        log.info("Wallet [{}] completed for userId: {}. txnId: {}, amount: {}, newBalance: {}",
                isCredit ? "CREDIT" : "DEBIT", userId, txn.getId(), amount, newBalance);
        return txn;
    }
}
