package com.trustai.transaction_service.service.impl;

import com.trustai.common.enums.TransactionType;
import com.trustai.common.utils.DateUtils;
import com.trustai.transaction_service.dto.response.WithdrawHistoryItem;
import com.trustai.transaction_service.entity.PendingWithdraw;
import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.exception.TransactionException;
import com.trustai.transaction_service.repository.PendingWithdrawRepository;
import com.trustai.transaction_service.repository.TransactionRepository;
import com.trustai.transaction_service.service.WalletService;
import com.trustai.transaction_service.service.WithdrawalService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class WithdrawalServiceImpl implements WithdrawalService {
    private final PendingWithdrawRepository pendingWithdrawRepository;
    private final TransactionRepository transactionRepository;
    private final WalletService walletService;

    @Override
    @Transactional
    public PendingWithdraw requestWithdraw(long userId, @NonNull BigDecimal amount, String remarks) {
        BigDecimal balance = walletService.getWalletBalance(userId);
        if (amount.compareTo(balance) > 0) {
            throw new TransactionException("Insufficient balance for withdrawal");
        }

        PendingWithdraw withdraw = new PendingWithdraw();
        withdraw.setUserId(userId);
        withdraw.setAmount(amount);
        withdraw.setStatus(PendingWithdraw.WithdrawStatus.PENDING);
        withdraw.setRemarks(remarks);

        return pendingWithdrawRepository.save(withdraw);
    }

    @Override
    @Transactional
    public PendingWithdraw approveWithdraw(long withdrawId, String approver) {
        PendingWithdraw withdraw = pendingWithdrawRepository.findById(withdrawId)
                .orElseThrow(() -> new TransactionException("Withdraw request not found"));

        if (withdraw.getStatus() != PendingWithdraw.WithdrawStatus.PENDING) {
            throw new TransactionException("Only pending withdrawals can be approved");
        }

        BigDecimal currentBalance = walletService.getWalletBalance(withdraw.getUserId());
        if (withdraw.getAmount().compareTo(currentBalance) > 0) {
            throw new TransactionException("Insufficient wallet balance");
        }

        // Deduct from wallet
        walletService.updateBalanceFromTransaction(withdraw.getUserId(), withdraw.getAmount().negate());

        // Save transaction record
        Transaction txn = new Transaction(
                withdraw.getUserId(),
                withdraw.getAmount(),
                TransactionType.WITHDRAWAL,
                currentBalance.subtract(withdraw.getAmount()),
                true
        );
        txn.setStatus(Transaction.TransactionStatus.SUCCESS);
        //txn.setTxnRefId(withdraw.getTxnRefId());
        transactionRepository.save(txn);

        // Update withdrawal
        withdraw.setStatus(PendingWithdraw.WithdrawStatus.APPROVED);
        withdraw.setApprovedBy(approver);
        withdraw.setApprovedAt(LocalDateTime.now());

        return pendingWithdrawRepository.save(withdraw);
    }

    @Override
    @Transactional
    public PendingWithdraw rejectWithdraw(long withdrawId, String approver, String rejectReason) {
        PendingWithdraw withdraw = pendingWithdrawRepository.findById(withdrawId)
                .orElseThrow(() -> new TransactionException("Withdraw request not found"));

        if (withdraw.getStatus() != PendingWithdraw.WithdrawStatus.PENDING) {
            throw new TransactionException("Only pending withdrawals can be rejected");
        }

        withdraw.setStatus(PendingWithdraw.WithdrawStatus.REJECTED);
        withdraw.setRejectedBy(approver);
        withdraw.setRejectedAt(LocalDateTime.now());
        withdraw.setRejectionReason(rejectReason);

        return pendingWithdrawRepository.save(withdraw);
    }

    @Override
    public Page<WithdrawHistoryItem> getPendingWithdrawHistory(@Nullable Long userId, Pageable pageable) {
        Page<PendingWithdraw> transactions;

        if (userId == null) { // admin
            transactions = pendingWithdrawRepository.findAll(pageable);
        } else {
            transactions = pendingWithdrawRepository.findByUserId(userId, pageable);
        }

        return transactions.map(withdraw -> new WithdrawHistoryItem(
                withdraw.getId(),
                null,
                withdraw.getAmount(),
                BigDecimal.ZERO,
                withdraw.getStatus().name(),
                DateUtils.formatDisplayDate(withdraw.getCreatedAt())
        ));
    }

    @Override
    public Page<WithdrawHistoryItem> getWithdrawHistory(@Nullable Long userId, Pageable pageable) {
        Page<Transaction> transactions;

        if (userId == null) { // admin
            transactions = transactionRepository.findByTxnTypeAndStatus(TransactionType.WITHDRAWAL, Transaction.TransactionStatus.SUCCESS, pageable);
        } else {
            String userIdStr = String.valueOf(userId);
            transactions = transactionRepository.findByUserIdAndTxnType(userIdStr, TransactionType.WITHDRAWAL, pageable);
        }

        return transactions.map(withdraw -> new WithdrawHistoryItem(
                withdraw.getId(),
                null,
                withdraw.getAmount(),
                BigDecimal.ZERO,
                withdraw.getStatus().name(),
                DateUtils.formatDisplayDate(withdraw.getCreatedAt())
        ));
    }
}
