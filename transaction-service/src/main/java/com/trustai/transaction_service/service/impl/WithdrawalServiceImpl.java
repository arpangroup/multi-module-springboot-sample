package com.trustai.transaction_service.service.impl;

import com.trustai.common.api.UserApi;
import com.trustai.common.dto.UserHierarchyDto;
import com.trustai.common.dto.UserInfo;
import com.trustai.common.enums.TransactionType;
import com.trustai.common.utils.DateUtils;
import com.trustai.transaction_service.config.WithdrawConfigProperty;
import com.trustai.transaction_service.entity.WithdrawRule;
import com.trustai.transaction_service.config.WithdrawRuleConfigCache;
import com.trustai.transaction_service.dto.response.WithdrawHistoryItem;
import com.trustai.transaction_service.entity.PendingWithdraw;
import com.trustai.transaction_service.entity.Transaction;
import com.trustai.transaction_service.exception.TransactionException;
import com.trustai.transaction_service.repository.PendingWithdrawRepository;
import com.trustai.transaction_service.repository.TransactionRepository;
import com.trustai.transaction_service.service.WalletService;
import com.trustai.transaction_service.service.WithdrawNotificationService;
import com.trustai.transaction_service.service.WithdrawalService;
import com.trustai.transaction_service.util.TransactionRemarks;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WithdrawalServiceImpl implements WithdrawalService {
    private final PendingWithdrawRepository pendingWithdrawRepository;
    private final TransactionRepository transactionRepository;
    private final WalletService walletService;
    private final UserApi userApi;
    private final WithdrawConfigProperty withdrawConfig;
    private final WithdrawNotificationService withdrawNotificationService;
    private final WithdrawRuleConfigCache withdrawRuleConfigCache;

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
                withdraw.getTxnRefId(),
                withdraw.getAmount(),
                withdraw.getServiceCharge(),
                withdraw.getStatus().name(),
                DateUtils.formatDisplayDate(withdraw.getCreatedAt()),
                withdraw.getWalletAddress(),
                withdraw.getCreatedBy()
        ));
    }

    /*@Override
    @Transactional
    public PendingWithdraw requestWithdraw(long userId, @NonNull BigDecimal withdrawAmount, String remarks) {
        UserInfo userInfo = userApi.getUserById(userId);
        BigDecimal walletBalance = userInfo.getWalletBalance();
        String walletAddress = userInfo.getWalletAddress();
        String rankCode = userInfo.getRankCode();

        // 1️⃣ Pre-calculate max withdraw and service charge
        BigDecimal maxWithdrawAllowed  = calculateMaxWithdrawAllowed(walletBalance, rankCode);
        BigDecimal appliedServiceCharge = calculateServiceCharge(withdrawAmount, withdrawConfig);
        //BigDecimal totalDeduction = withdrawAmount.add(appliedServiceCharge);

        // 2️⃣ Check for existing pending withdraw
        boolean hasPendingWithdraw = pendingWithdrawRepository.existsByUserIdAndStatus(userId, PendingWithdraw.WithdrawStatus.PENDING);
        if (hasPendingWithdraw) {
            throw new TransactionException("There is already a pending withdraw request for this user.");
        }

        // 3️⃣ Check minimum withdraw amount
        if (withdrawAmount.compareTo(withdrawConfig.getAmountMin()) < 0) {
            throw new TransactionException("Withdrawal amount must be at least " + withdrawConfig.getAmountMin());
        }

        // 4️⃣ Calculate max allowed withdraw based on rank
        if (withdrawAmount.compareTo(maxWithdrawAllowed) > 0) {
            throw new TransactionException( "You can withdraw a maximum of " + maxWithdrawAllowed + " based on your current rank.");
        }

        // 5️⃣ Check if user has enough balance including service charge
        *//*if (totalDeduction.compareTo(walletBalance) > 0) {
            throw new TransactionException("Insufficient balance to cover withdrawal and service charge.");
        }*//*

        // Deduct only the requested withdraw amount
        if (withdrawAmount.compareTo(walletBalance) > 0) {
            throw new TransactionException("Insufficient wallet balance.");
        }

        // 6️⃣ Deduct immediately from wallet
        Transaction transaction = walletService.updateWalletBalance(
                userId,
                withdrawAmount,        // deduct only withdraw amount from wallet
                appliedServiceCharge,  // track service charge for system accounting
                TransactionType.WITHDRAWAL,
                "withdraw-service",
                false,
                TransactionRemarks.WITHDRAW_REQUESTED,
                null
        );

        // 7️⃣ Create PendingWithdraw record
        PendingWithdraw withdraw = new PendingWithdraw();
        withdraw.setTxnRefId(transaction.getTxnRefId());
        withdraw.setUserId(userId);
        withdraw.setAmount(withdrawAmount);
        withdraw.setServiceCharge(appliedServiceCharge);
        withdraw.setWalletAddress(walletAddress);
        withdraw.setRankCode(rankCode); // <-- store rankCode for attempt tracking
        withdraw.setStatus(PendingWithdraw.WithdrawStatus.PENDING);
        withdraw.setRemarks(remarks);

        // 8️⃣ Publish notification
        withdrawNotificationService.publishWithdrawRequestReceived(userInfo, withdraw);

        return pendingWithdrawRepository.save(withdraw);
    }*/

    @Override
    @Transactional
    public PendingWithdraw requestWithdraw(long userId, @NonNull BigDecimal withdrawAmount, String remarks) {
        UserInfo userInfo = userApi.getUserById(userId);
        BigDecimal walletBalance = userInfo.getWalletBalance();
        String walletAddress = userInfo.getWalletAddress();
        String rankCode = userInfo.getRankCode();

        // 1️⃣ Pre-calculate max withdraw and service charge
        BigDecimal maxWithdrawAllowed  = calculateMaxWithdrawAllowed(walletBalance, rankCode);
        BigDecimal appliedServiceCharge = calculateServiceCharge(withdrawAmount, withdrawConfig);
        //BigDecimal totalDeduction = withdrawAmount.add(appliedServiceCharge);

        // 2️⃣ Check for existing pending withdraw
        boolean hasPendingWithdraw = pendingWithdrawRepository.existsByUserIdAndStatus(userId, PendingWithdraw.WithdrawStatus.PENDING);
        if (hasPendingWithdraw) {
            throw new TransactionException("There is already a pending withdraw request for this user.");
        }

        // 3️⃣ Check minimum withdraw amount
        if (withdrawAmount.compareTo(withdrawConfig.getAmountMin()) < 0) {
            throw new TransactionException("Withdrawal amount must be at least " + withdrawConfig.getAmountMin());
        }

        // Validate rank rules (members & referrals)
        validateWithdrawRules(userId, rankCode);

        // 4️⃣ Calculate max allowed withdraw based on rank
        if (withdrawAmount.compareTo(maxWithdrawAllowed) > 0) {
            throw new TransactionException( "You can withdraw a maximum of " + maxWithdrawAllowed + " based on your current rank.");
        }

        // 5️⃣ Check if user has enough balance including service charge
        /*if (totalDeduction.compareTo(walletBalance) > 0) {
            throw new TransactionException("Insufficient balance to cover withdrawal and service charge.");
        }*/

        // Deduct only the requested withdraw amount
        if (withdrawAmount.compareTo(walletBalance) > 0) {
            throw new TransactionException("Insufficient wallet balance.");
        }

        // 6️⃣ Deduct immediately from wallet
        Transaction transaction = walletService.updateWalletBalance(
                userId,
                withdrawAmount,        // deduct only withdraw amount from wallet
                appliedServiceCharge,  // track service charge for system accounting
                TransactionType.WITHDRAWAL,
                "withdraw-service",
                false,
                TransactionRemarks.WITHDRAW_REQUESTED,
                null
        );

        // 7️⃣ Create PendingWithdraw record
        PendingWithdraw withdraw = new PendingWithdraw();
        withdraw.setTxnRefId(transaction.getTxnRefId());
        withdraw.setUserId(userId);
        withdraw.setAmount(withdrawAmount);
        withdraw.setServiceCharge(appliedServiceCharge);
        withdraw.setWalletAddress(walletAddress);
        withdraw.setRankCode(rankCode); // <-- store rankCode for attempt tracking
        withdraw.setStatus(PendingWithdraw.WithdrawStatus.PENDING);
        withdraw.setRemarks(remarks);

        // 8️⃣ Publish notification
        withdrawNotificationService.publishWithdrawRequestReceived(userInfo, withdraw);

        return pendingWithdrawRepository.save(withdraw);
    }

    private BigDecimal calculateServiceCharge(BigDecimal withdrawAmount, WithdrawConfigProperty config) {
        BigDecimal appliedServiceCharge;
        if (withdrawAmount.compareTo(config.getServiceChargeThreshold()) < 0) {
            appliedServiceCharge = config.getServiceChargeFixed();
        } else {
            appliedServiceCharge = withdrawAmount.multiply(config.getServiceChargePercentage()).setScale(2, RoundingMode.HALF_UP);
        }
        return appliedServiceCharge;
    }

    private BigDecimal calculateMaxWithdrawAllowed(BigDecimal walletBalance, String rankCode) {
        /*
        // Get withdraw percentage from config map
        BigDecimal withdrawPercentage = withdrawConfig.getWithdrawLimitByRankMap()
                .getOrDefault(rankCode, BigDecimal.ONE); // default 100% if rank not found

        // Max withdraw = walletBalance * percentage
        return walletBalance.multiply(withdrawPercentage);
         */

        WithdrawRule rule = withdrawRuleConfigCache.findByRankCode(rankCode);
        if (rule.getMaxWithdrawAmount() != null && rule.getMaxWithdrawAmount().compareTo(BigDecimal.ZERO) > 0) {
            return walletBalance.min(rule.getMaxWithdrawAmount());
        }
        return walletBalance; // fallback: full balance allowed
    }

    @Override
    @Transactional
    public PendingWithdraw approveWithdraw(long withdrawId, String approver) {
        PendingWithdraw withdraw = pendingWithdrawRepository.findById(withdrawId)
                .orElseThrow(() -> new TransactionException("Withdraw request not found"));

        if (withdraw.getStatus() != PendingWithdraw.WithdrawStatus.PENDING) {
            throw new TransactionException("Only pending withdrawals can be approved");
        }

        // update the existing Transaction status
        String txnRefId = withdraw.getTxnRefId();
        Transaction transaction = transactionRepository.findByTxnRefId(txnRefId)
                .orElseThrow(() -> new TransactionException("txnRefId not found"));
        transaction.setRemarks(TransactionRemarks.WITHDRAW_APPROVED);
        transaction.setStatus(Transaction.TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);

        // Update withdrawal
        withdraw.setStatus(PendingWithdraw.WithdrawStatus.APPROVED);
        withdraw.setApprovedBy(approver);
        withdraw.setApprovedAt(LocalDateTime.now());
        withdraw = pendingWithdrawRepository.save(withdraw);


        // publish withdraw approved notification...
        UserInfo userInfo = userApi.getUserById(withdraw.getUserId());
        withdrawNotificationService.publishWithdrawApproved(userInfo, withdraw);

        return withdraw;
    }

    @Override
    @Transactional
    public PendingWithdraw rejectWithdraw(long withdrawId, String approver, String rejectReason) {
        PendingWithdraw withdraw = pendingWithdrawRepository.findById(withdrawId)
                .orElseThrow(() -> new TransactionException("Withdraw request not found"));

        if (withdraw.getStatus() != PendingWithdraw.WithdrawStatus.PENDING) {
            throw new TransactionException("Only pending withdrawals can be rejected");
        }

        // update the existing Transaction status
        String txnRefId = withdraw.getTxnRefId();
        Transaction transaction = transactionRepository.findByTxnRefId(txnRefId)
                .orElseThrow(() -> new TransactionException("txnRefId not found"));
        transaction.setRemarks(TransactionRemarks.WITHDRAW_REJECTED);
        transaction.setStatus(Transaction.TransactionStatus.REFUNDED);
        transactionRepository.save(transaction);

        // Refund wallet
        walletService.updateWalletBalance(
                withdraw.getUserId(),
                withdraw.getAmount(),  //  refund full requested amount
                BigDecimal.ZERO,       // no service charge when refund
                TransactionType.REFUND,
                "withdraw-service",
                true,
                TransactionRemarks.WITHDRAW_REJECTED + ":" + rejectReason,
                null
        );

        withdraw.setStatus(PendingWithdraw.WithdrawStatus.REJECTED);
        withdraw.setRejectedBy(approver);
        withdraw.setRejectedAt(LocalDateTime.now());
        withdraw.setRejectionReason(rejectReason);
        withdraw = pendingWithdrawRepository.save(withdraw);


        // publish withdraw approved notification...
        UserInfo userInfo = userApi.getUserById(withdraw.getUserId());
        withdrawNotificationService.publishWithdrawRejected(userInfo, withdraw);

        return withdraw;
    }

    private void validateWithdrawAttempts(Long userId, String rankCode) {
        BigDecimal withdrawPercentage = withdrawConfig.getWithdrawLimitByRankMap().getOrDefault(rankCode, BigDecimal.ONE);

        // If rank is allowed full withdraw (100%), skip attempt restriction
        if (withdrawPercentage.compareTo(BigDecimal.ONE) == 0) {
            return; // unlimited
        }

        // Otherwise restrict attempts
        int usedAttempts = pendingWithdrawRepository.countByUserIdAndRankCodeAndStatus(
                userId,
                rankCode,
                PendingWithdraw.WithdrawStatus.APPROVED
        );

        if (usedAttempts >= 1) {  // 🔄 configurable if needed
            throw new TransactionException("You have already used your maximum withdraw limit for rank " + rankCode);
        }
    }

    private void validateWithdrawRules(long userId, String rankCode) {
        WithdrawRule rule = withdrawRuleConfigCache.findByRankCode(rankCode);


        List<UserHierarchyDto> descendants = userApi.fetchDownline(userId);
        log.debug("Found {} descendants for userId={}", descendants.size(), userId);

        // Count total members
        long totalMembers = descendants.stream()
                .filter(UserHierarchyDto::isActive)
                .count();

        // Count direct referrals (depth = 1)
        long directReferrals = descendants.stream()
                .filter(UserHierarchyDto::isActive)
                .filter(dto -> dto.getDepth() == 1)
                .count();
        log.info("User {} has {} active team members and {} direct referrals", userId, totalMembers, directReferrals);

//        if (totalMembers < rule.getRequiredTotalMembers()) {
//            throw new TransactionException("You need at least " + rule.getRequiredTotalMembers() +
//                    " active team members to withdraw with rank " + rankCode);
//        }

        if (directReferrals < rule.getRequiredDirectReferrals()) {
            log.warn("User {} does not meet direct referral requirement: has={}, required={}", userId, directReferrals, rule.getRequiredDirectReferrals());
            throw new TransactionException("You need at least " + rule.getRequiredDirectReferrals() +
                    " direct referrals to withdraw with rank " + rankCode);
        }

        // Check withdraw attempts
        int approvedAttempts = pendingWithdrawRepository.countByUserIdAndRankCodeAndStatus(
                userId,
                rankCode,
                PendingWithdraw.WithdrawStatus.APPROVED
        );

        if (approvedAttempts >= rule.getWithdrawLimit()) {
            log.warn("User {} has reached withdraw limit: attempts={}, limit={}", userId, approvedAttempts, rule.getWithdrawLimit());
            throw new TransactionException("You have already used your maximum withdraw attempts for rank " + rankCode);
        }
    }

}
