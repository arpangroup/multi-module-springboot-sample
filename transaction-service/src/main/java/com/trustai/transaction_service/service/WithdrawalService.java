package com.trustai.transaction_service.service;

import com.trustai.transaction_service.dto.response.WithdrawHistoryItem;
import com.trustai.transaction_service.entity.PendingWithdraw;
import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.exception.InsufficientBalanceException;
import com.trustai.transaction_service.exception.InvalidPaymentGatewayException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;

public interface WithdrawalService {

    Page<WithdrawHistoryItem> getPendingWithdrawHistory(Long userId, Pageable pageable);
    Page<WithdrawHistoryItem> getWithdrawHistory(@Nullable Long userId, Pageable pageable);

    /**
     * Processes a withdrawal request made by the user.
     * <p>
     * This method deducts the specified amount from the user's balance and initiates
     * a transfer to the provided destination account, which could be a bank account,
     * wallet, or any external financial address.
     *
     * @param userId             the ID of the user requesting the withdrawal.
     *                           This identifies whose balance will be deducted.
     *
     * @param amount             the amount to be withdrawn (must be positive and non-null).
     *                           Ensure that the user has sufficient balance before calling this method.
     *
     * @param destinationAccount the account or destination to which the funds should be transferred.
     *                           This could be a bank account number, mobile wallet ID, crypto address, etc.
     *
     * @param remarks            optional remarks or notes related to the withdrawal.
     *                           This may be displayed in transaction history or audit logs.
     *
     * @return a {@link Transaction} object representing the withdrawal transaction,
     *         including status and reference details.
     *
     * @throws InsufficientBalanceException if the user does not have enough funds.
     * @throws InvalidPaymentGatewayException if the destination account or channel is not supported.
     */
    PendingWithdraw requestWithdraw(long userId, @NonNull BigDecimal amount, String remarks);

    PendingWithdraw approveWithdraw(long withdrawId, String approver);
    PendingWithdraw rejectWithdraw(long withdrawId, String approver, String rejectReason);


}
