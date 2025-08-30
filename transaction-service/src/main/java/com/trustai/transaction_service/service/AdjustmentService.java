package com.trustai.transaction_service.service;

import com.trustai.transaction_service.entity.Transaction;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;

public interface AdjustmentService {

    /**
     * Adds a specified amount to the user's balance as a manual adjustment.
     * <p>
     * This method is typically used for administrative actions such as applying
     * goodwill credits, resolving underpayments, or manually adjusting balances.
     * The credit is applied directly and should be justified with a clear reason
     * to maintain transparency and auditability.
     *
     * @param userId the ID of the user whose account will be credited
     * @param amount the amount to be added (must be non-null and positive)
     * @param reason a descriptive explanation for the credit (e.g., "Goodwill Credit", "Payment Correction")
     * @return a {@link Transaction} object representing the manual credit
     * @throws IllegalArgumentException if the amount is invalid or the reason is missing
     */
    Transaction addBalance(long userId, @NonNull BigDecimal amount, String reason);

    /**
     * Deducts a specified amount from the user's balance as a manual adjustment.
     * <p>
     * This method is typically used for administrative actions such as applying
     * penalties, correcting overpayments, or making internal balance adjustments.
     * The deduction is performed directly and should be accompanied by a clear reason
     * to ensure transparency and traceability.
     *
     * @param userId the ID of the user whose account will be debited
     * @param amount the amount to be subtracted (must be non-null and positive)
     * @param reason a descriptive explanation for the deduction (e.g., "Overdraft Fee", "Reversal of Bonus")
     * @return a {@link Transaction} object representing the manual subtraction
     * @throws IllegalArgumentException if the amount is invalid or reason is missing
     */
    Transaction subtractBalance(long userId, @NonNull BigDecimal amount, String reason);
}
