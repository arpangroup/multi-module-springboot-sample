package com.trustai.transaction_service.service.impl;

import com.trustai.common.enums.PaymentGateway;
import com.trustai.common.enums.TransactionType;
import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.repository.TransactionRepository;
import com.trustai.transaction_service.service.TransferService;
import com.trustai.transaction_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {
    private final TransactionRepository transactionRepository;
    private final WalletService walletService;

    @Override
    public Transaction transferMoney(long senderId, long receiverId, BigDecimal amount, String message) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero.");
        }

        // Debit sender
        BigDecimal senderBalance = walletService.getWalletBalance(senderId).subtract(amount);
        Transaction debitTxn = new Transaction(senderId, amount, TransactionType.SEND_MONEY, senderBalance, false);
        debitTxn.setSenderId(senderId);
        debitTxn.setGateway(PaymentGateway.SYSTEM);
        debitTxn.setStatus(Transaction.TransactionStatus.SUCCESS);
        debitTxn.setRemarks("Transfer to user " + receiverId);
        debitTxn.setMetaInfo(message);
        transactionRepository.save(debitTxn);
        walletService.updateBalanceFromTransaction(senderId, amount.negate());

        // Credit receiver
        BigDecimal receiverBalance = walletService.getWalletBalance(receiverId).add(amount);
        Transaction creditTxn = new Transaction(receiverId, amount, TransactionType.RECEIVE_MONEY, receiverBalance, true);
        creditTxn.setSenderId(senderId);
        creditTxn.setGateway(PaymentGateway.SYSTEM);
        creditTxn.setStatus(Transaction.TransactionStatus.SUCCESS);
        creditTxn.setRemarks("Received from user " + senderId);
        creditTxn.setMetaInfo(message);
        transactionRepository.save(creditTxn);
        walletService.updateBalanceFromTransaction(receiverId, amount);

        return creditTxn;
    }
}
