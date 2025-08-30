package com.trustai.transaction_service.service;

import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.exception.InsufficientBalanceException;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;

public interface TransferService {

    /**
     * Transfers money from one user to another (peer-to-peer transfer).
     * <p>
     * This method debits the sender's account and credits the receiver's account
     * in a single atomic transaction.
     *
     * @param senderId    the ID of the user who is sending the money.
     * @param receiverId  the ID of the user who will receive the money.
     * @param amount      the amount to be transferred (must be positive and non-null).
     * @param message     optional message or note attached to the transaction (e.g., "Thanks!", "For lunch").
     *
     * @return a {@link Transaction} object representing the sender's side of the transaction.
     *
     * @throws InsufficientBalanceException if the sender does not have enough funds.
     * @throws IllegalArgumentException if sender and receiver are the same.
     */
    Transaction transferMoney(long senderId, long receiverId, @NonNull BigDecimal amount, String message); // sendMoney / receiveMoney

}
