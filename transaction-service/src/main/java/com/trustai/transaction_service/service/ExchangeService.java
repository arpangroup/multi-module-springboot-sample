package com.trustai.transaction_service.service;

import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.exception.InsufficientBalanceException;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;

public interface ExchangeService {

    /**
     * Records a currency or asset exchange transaction for a user.
     * <p>
     * This method is used when a user converts one currency or asset into another.
     * It logs both the amount deducted in the original currency and the amount credited
     * in the new currency. Common use cases include fiat-to-crypto, crypto-to-crypto,
     * or foreign currency exchange.
     *
     * @param userId       the ID of the user performing the exchange.
     *                     The original amount will be deducted from this user's balance.
     *
     * @param fromAmount   the amount to be deducted from the original currency (must be non-null and positive).
     *                     This represents the value the user is giving up in the exchange.
     *
     * @param fromCurrency the code of the currency or asset being exchanged from (e.g., "USD", "BTC", "ETH").
     *                     Must follow standard currency or asset symbol conventions.
     *
     * @param toAmount     the amount to be credited in the target currency (must be non-null and positive).
     *                     This is the result of the exchange rate applied to the original amount.
     *
     * @param toCurrency   the code of the currency or asset being exchanged to (e.g., "EUR", "USDT", "ETH").
     *                     Should be different from `fromCurrency` in a valid exchange.
     *
     * @param metaInfo     optional metadata related to the exchange, such as the exchange rate,
     *                     transaction fee, or exchange platform details. This can be a JSON string or descriptive text.
     *
     * @return a {@link Transaction} object representing the exchange transaction,
     *         typically categorized as a single logical operation containing both sides of the exchange.
     *
     * @throws InsufficientBalanceException if the user does not have enough of the `fromCurrency` to perform the exchange.
     * @throws IllegalArgumentException if currency codes are invalid or the amounts are inconsistent.
     */
    Transaction exchange(long userId, @NonNull BigDecimal fromAmount, @NonNull String fromCurrency, @NonNull BigDecimal toAmount, @NonNull String toCurrency, String metaInfo);

}
