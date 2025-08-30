//package com.trustai.transaction_service.service;
//
//import com.trustai.transaction_service.exception.InsufficientBalanceException;
//import com.trustai.transaction_service.exception.InvalidPaymentGatewayException;
//import com.trustai.transaction_service.dto.request.DepositRequest;
//import com.trustai.transaction_service.entity.Transaction;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.lang.NonNull;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//
///**
// * Service interface for handling user transactions such as deposits, withdrawals, transfers, and bonuses.
// * <p>
// * This interface defines the contract for transaction-related operations within the system,
// * including methods for recording financial activities like deposits, withdrawals,
// * peer-to-peer transfers, investment tracking, and system-generated bonuses.
// *
// * <p>Implementations of this interface are responsible for ensuring transaction integrity,
// * validation, and consistent audit logging.
// *
// * @author Arpan Jana
// * @since 1.0
// */
//
//@Service
//@RequiredArgsConstructor
//public class TransactionServiceFacade {
//    private final TransactionQueryService transactionQueryService;
//    private final DepositService depositService;
//    private final WithdrawalService withdrawalService;
//    private final TransferService transferService;
//    private final BonusTransactionService bonusService;
//    private final InvestmentTransactionService investmentService;
//    private final ExchangeService exchangeService;
//    private final AdjustmentService adjustmentService;
//    private final RefundService refundService;
//
//
//    Page<Transaction> getTransactions(Transaction.TransactionStatus status, Integer page, Integer size) {
//        return transactionQueryService.getTransactions(status, page, size);
//    }
//
//    Page<Transaction> getProfits(Transaction.TransactionStatus status, Integer page, Integer size) {
//        return transactionQueryService.getProfits(page, size);
//    }
//
//    public Page<Transaction> getTransactionsByUserId(Long userId, Integer page, Integer size) {
//        return transactionQueryService.getTransactionsByUserId(userId, page, size);
//    }
//
//    public Boolean hasDepositTransaction(Long userId) {
//        return transactionQueryService.hasDepositTransaction(userId);
//    }
//
//    public Transaction deposit(@NonNull DepositRequest depositRequest) {
//        return depositService.deposit(depositRequest);
//    }
//
//    public Transaction depositManual(long userId, long depositor, @NonNull BigDecimal amount, String remarks) {
//        return depositService.depositManual(userId, depositor, amount, remarks);
//    }
//
//    public Transaction withdraw(long userId, @NonNull BigDecimal amount, String destinationAccount, String remarks)
//            throws InsufficientBalanceException, InvalidPaymentGatewayException {
//        return withdrawalService.withdraw(userId, amount, destinationAccount, remarks);
//    }
//
//    public Transaction transferMoney(long senderId, long receiverId, @NonNull BigDecimal amount, String message)
//            throws InsufficientBalanceException {
//        return transferService.transferMoney(senderId, receiverId, amount, message);
//    }
//
//    public Transaction applySignupBonus(long userId, @NonNull BigDecimal bonusAmount) {
//        return bonusService.applySignupBonus(userId, bonusAmount);
//    }
//
//    public Transaction applyReferralBonus(long referrerUserId, long referredUserId, @NonNull BigDecimal bonusAmount) {
//        return bonusService.applyReferralBonus(referrerUserId, referredUserId, bonusAmount);
//    }
//
//    public Transaction applyBonus(long userId, @NonNull BigDecimal bonusAmount, String reason) {
//        return bonusService.applyBonus(userId, bonusAmount, reason);
//    }
//
//    public Transaction invest(long userId, @NonNull BigDecimal amount, String investmentType, String metaInfo) {
//        return investmentService.invest(userId, amount, investmentType, metaInfo);
//    }
//
//    public Transaction exchange(long userId, @NonNull BigDecimal fromAmount, @NonNull String fromCurrency,
//                                @NonNull BigDecimal toAmount, @NonNull String toCurrency, String metaInfo) {
//        return exchangeService.exchange(userId, fromAmount, fromCurrency, toAmount, toCurrency, metaInfo);
//    }
//
//    public Transaction subtract(long userId, @NonNull BigDecimal amount, String reason) {
//        return adjustmentService.subtractBalance(userId, amount, reason);
//    }
//
//    public Transaction refund(long userId, @NonNull BigDecimal amount, String originalTxnRef, String reason) {
//        return refundService.refund(userId, amount, originalTxnRef, reason);
//    }
//
//
//    // TODO: continue adding more delegation methods as needed
//}
