package com.trustai.transaction_service.service;

import com.trustai.transaction_service.entity.Transaction;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;

public interface RefundService {

    /**
     * Issues a refund to a user for a previously completed transaction.
     * <p>
     * This method is typically used when a transaction (such as a purchase,
     * deposit, or failed transfer) needs to be reversed, and the amount
     * should be returned to the user's balance.
     *
     * @param userId          the ID of the user receiving the refund.
     *                        This identifies the account to which the refund will be credited.
     *
     * @param amount          the amount to be refunded (must be non-null and positive).
     *                        Ensure it does not exceed the original transaction amount.
     *
     * @param originalTxnRef  the reference ID of the original transaction that is being refunded.
     *                        This is used for traceability and audit purposes.
     *
     * @param reason          a short description or reason for issuing the refund.
     *                        This can help in reporting and customer support follow-ups.
     *
     * @return a {@link Transaction} object representing the refund transaction,
     *         including status, timestamps, and references.
     *
     * @throws IllegalArgumentException if the original transaction is not found or not refundable.
     */
    Transaction refund(long userId, @NonNull BigDecimal amount, String originalTxnRef, String reason);

}
