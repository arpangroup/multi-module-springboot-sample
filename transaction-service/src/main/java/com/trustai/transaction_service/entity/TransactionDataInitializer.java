package com.trustai.transaction_service.entity;

import com.trustai.common.enums.PaymentGateway;
import com.trustai.common.enums.TransactionType;
import com.trustai.common.utils.EnumUtil;
import com.trustai.transaction_service.repository.TransactionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

//@Component
@RequiredArgsConstructor
public class TransactionDataInitializer {
    private final TransactionRepository transactionRepository;

    @PostConstruct
    public void init() {
        createTransaction(1L, "Withdraw", "-2000 INR", "WITHDRAWAL", "Pending");
        createTransaction(2L, "Investment", "-5000 INR", "SYSTEM", "Success");
        createTransaction(3L, "Refund", "5000 INR", "SYSTEM", "Pending");
        createTransaction(4L, "Subtract", "-5000 INR", "SYSTEM", "Cancelled");
        createTransaction(5L, "Withdraw", "-5000 INR", "WITHDRAWAL", "Cancelled");
        createTransaction(6L, "Investment", "-5000 INR", "SYSTEM", "Success");
        createTransaction(7L, "Signup Bonus", "-5000 INR", "SYSTEM", "Success");
        createTransaction(8L, "Deposit", "5000 INR", "SYSTEM", "Success");
        createTransaction(9L, "Refund", "-5000 INR", "SYSTEM", "Success");
        createTransaction(10L, "Referral", "-5000 INR", "SYSTEM", "Success");
        createTransaction(11L, "Exchange", "-5000 INR", "SYSTEM", "Success");
        createTransaction(12L, "Manual Deposit", "100 INR", "SYSTEM", "Cancelled");
        createTransaction(13L, "Interest", "-5000 INR", "SYSTEM", "Success");
        createTransaction(14L, "Bonus", "-5000 INR", "SYSTEM", "Success");
        createTransaction(15L, "Manual Deposit", "100 INR", "BINANCE", "Cancelled");
    }

    private void createTransaction(Long userId, String type, String amountStr, String gateway, String statusStr) {
        try {
            BigDecimal amount = parseAmount(amountStr);
            Transaction.TransactionStatus status = mapStatus(statusStr);
            TransactionType txnType = mapTransactionType(type);

            Transaction txn = new Transaction(userId, amount, txnType, BigDecimal.ZERO, true);

            txn.setTxnRefId("TRXPOZ5ZZYDTJ"); // or UUID.randomUUID().toString()
            txn.setStatus(status);
            txn.setGateway(EnumUtil.fromString(PaymentGateway.class, gateway));
            txn.setRemarks("");
            txn.setTxnFee(BigDecimal.ZERO);
            txn.setMetaInfo("Initialized by TransactionDataInitializer");

            transactionRepository.save(txn);
        } catch (Exception e) {
            System.err.println("❌ Failed to create transaction for userId " + userId + ": " + e.getMessage());
        }
    }

    private BigDecimal parseAmount(String amountStr) {
        return new BigDecimal(amountStr.replace("INR", "").trim());
    }

    private Transaction.TransactionStatus mapStatus(String statusStr) {
        return switch (statusStr.toUpperCase()) {
            case "PENDING" -> Transaction.TransactionStatus.PENDING;
            case "SUCCESS", "COMPLETED" -> Transaction.TransactionStatus.SUCCESS;
            case "FAILED" -> Transaction.TransactionStatus.FAILED;
            case "CANCELLED" -> Transaction.TransactionStatus.CANCELLED;
            default -> Transaction.TransactionStatus.PENDING;
        };
    }

    private TransactionType mapTransactionType(String typeStr) {
        // Replace spaces with underscores and convert to uppercase for enum matching
        return TransactionType.valueOf(typeStr.replace(" ", "_").toUpperCase());
    }
}
