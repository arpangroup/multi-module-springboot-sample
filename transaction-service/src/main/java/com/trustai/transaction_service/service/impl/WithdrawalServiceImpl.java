package com.trustai.transaction_service.service.impl;

import com.trustai.common.api.UserApi;
import com.trustai.common.dto.UserInfo;
import com.trustai.common.enums.TransactionType;
import com.trustai.common.utils.DateUtils;
import com.trustai.transaction_service.config.WithdrawConfigProperty;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class WithdrawalServiceImpl implements WithdrawalService {
    private final PendingWithdrawRepository pendingWithdrawRepository;
    private final TransactionRepository transactionRepository;
    private final WalletService walletService;
    private final UserApi userApi;
    private final WithdrawConfigProperty withdrawConfig;

    @Override
    @Transactional
    public PendingWithdraw requestWithdraw(long userId, @NonNull BigDecimal withdrawAmount, String remarks) {
        UserInfo userInfo = userApi.getUserById(userId);

        // Check if there is already a pending withdraw request for this user
        boolean hasPendingWithdraw = pendingWithdrawRepository.existsByUserIdAndStatus(userId, PendingWithdraw.WithdrawStatus.PENDING);
        if (hasPendingWithdraw) {
            throw new TransactionException("There is already a pending withdraw request for this user.");
        }

        String walletAddress = userInfo.getWalletAddress();
        BigDecimal walletBalance = userInfo.getWalletBalance();

        // ✅ Check if withdrawAmount is below the minimum
        if (withdrawAmount.compareTo(withdrawConfig.getAmountMin()) < 0) {
            throw new TransactionException("Withdrawal amount must be at least " + withdrawConfig.getAmountMin());
        }

        // ✅ Calculate service charge based on threshold
        BigDecimal appliedServiceCharge;
        if (withdrawAmount.compareTo(withdrawConfig.getServiceChargeThreshold()) < 0) {
            appliedServiceCharge = withdrawConfig.getServiceChargeFixed();
        } else {
            appliedServiceCharge = withdrawAmount.multiply(withdrawConfig.getServiceChargePercentage()).setScale(2, RoundingMode.HALF_UP);
        }

        // ✅ Check if user has enough balance including service charge
        BigDecimal totalDeduction = withdrawAmount.add(appliedServiceCharge);
        if (totalDeduction.compareTo(walletBalance) > 0) {
            throw new TransactionException("Insufficient balance to cover withdrawal and service charge.");
        }

        PendingWithdraw withdraw = new PendingWithdraw();
        withdraw.setUserId(userId);
        withdraw.setAmount(withdrawAmount);
        withdraw.setServiceCharge(appliedServiceCharge);
        withdraw.setWalletAddress(walletAddress);
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
        BigDecimal totalDeductAmount = withdraw.getAmount().add(withdraw.getServiceCharge());
        BigDecimal serviceCharge = withdraw.getServiceCharge();

        if (totalDeductAmount.compareTo(currentBalance) > 0) {
            throw new TransactionException("Insufficient wallet balance");
        }

        log.info("Approving withdrawal. UserID: {}, Current Balance: {}, Total Deduct Amount: {} (Amount: {}, Service Charge: {})",
                withdraw.getUserId(), currentBalance, totalDeductAmount, withdraw.getAmount(), serviceCharge);
        Transaction txn = walletService.updateWalletBalance(
                withdraw.getUserId(),
                totalDeductAmount,
                serviceCharge,
                TransactionType.WITHDRAWAL,
                "withdraw-service",
                false,
                "Withdrawal approved",
                null
        );

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

    /*@Override
    public Page<WithdrawHistoryItem> getPendingWithdrawHistory(@Nullable Long userId, Pageable pageable) {
        Page<PendingWithdraw> transactions;

        if (userId == null) { // admin
            transactions = pendingWithdrawRepository.findByStatus( PendingWithdraw.WithdrawStatus.PENDING, pageable);
        } else {
            transactions = pendingWithdrawRepository.findByUserIdAndStatus(userId, PendingWithdraw.WithdrawStatus.PENDING, pageable);
        }

        return transactions.map(withdraw -> new WithdrawHistoryItem(
                withdraw.getId(),
                null,
                withdraw.getAmount(),
                withdraw.getServiceCharge(),
                withdraw.getStatus().name(),
                DateUtils.formatDisplayDate(withdraw.getCreatedAt()),
                withdraw.getWalletAddress(),
                withdraw.getCreatedBy()
        ));
    }*/

    @Override
    public Page<WithdrawHistoryItem> getWithdrawHistory(@Nullable Long userId, PendingWithdraw.WithdrawStatus status, Pageable pageable) {
        Sort sortByIdDesc = Sort.by(Sort.Direction.DESC, "id");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortByIdDesc);

        Page<PendingWithdraw> withdraws;

        if (userId == null) { // Admin
            if (status == null) {
                withdraws = pendingWithdrawRepository.findAll(sortedPageable);
            } else {
                withdraws = pendingWithdrawRepository.findByStatus(status, sortedPageable);
            }
        } else { // User
            if (status == null) {
                withdraws = pendingWithdrawRepository.findByUserId(userId, sortedPageable);
            } else {
                withdraws = pendingWithdrawRepository.findByUserIdAndStatus(userId, status, sortedPageable);
            }
        }

        return withdraws.map(withdraw -> new WithdrawHistoryItem(
                withdraw.getId(),
                null,
                withdraw.getAmount(),
                withdraw.getServiceCharge(),
                withdraw.getStatus().name(),
                DateUtils.formatDisplayDate(withdraw.getCreatedAt()),
                withdraw.getWalletAddress(),
                withdraw.getCreatedBy()
        ));
    }
}
