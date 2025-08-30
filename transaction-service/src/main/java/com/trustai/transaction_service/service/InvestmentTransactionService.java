package com.trustai.transaction_service.service;

import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.exception.InsufficientBalanceException;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;

public interface InvestmentTransactionService {

    /**
     * Records an investment transaction made by the user.
     * <p>
     * This method is used when a user invests a certain amount into a financial product,
     * plan, or asset. The invested amount is deducted from the user's balance and recorded
     * along with metadata about the investment.
     *
     * @param userId         the ID of the user making the investment.
     *                       This identifies whose account will be debited.
     *
     * @param amount         the amount being invested (must be non-null and positive).
     *                       Ensure the user has sufficient balance before proceeding.
     *
     * @param investmentType the type or category of the investment (e.g., "Mutual Fund", "Fixed Deposit", "Crypto").
     *                       Used to classify the investment for reporting or processing purposes.
     *
     * @param metaInfo       optional additional information related to the investment.
     *                       This could be a JSON string or metadata including duration, expected return, etc.
     *
     * @return a {@link Transaction} object representing the investment transaction,
     *         including status and reference details.
     *
     * @throws InsufficientBalanceException if the user does not have enough funds to invest.
     * @throws IllegalArgumentException if investmentType is invalid or not supported.
     */
    Transaction invest(long userId, @NonNull BigDecimal amount, String investmentType, String metaInfo);

}
