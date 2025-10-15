package com.trustai.transaction_service.service.impl;

import com.trustai.common.api.UserApi;
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
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@RefreshScope
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
    public PendingWithdraw requestWithdrawV1(long userId, @NonNull BigDecimal withdrawAmount, String remarks) {
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

    /*@Override
    @Transactional
    public PendingWithdraw requestWithdrawV2(long userId, @NonNull BigDecimal withdrawAmount, String remarks) {
        UserInfo userInfo = userApi.getUserById(userId);
        BigDecimal walletBalance = userInfo.getWalletBalance();
        BigDecimal profitBalance = userInfo.getProfitWallet();
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
        validateWithdrawRules(userId, rankCode, withdrawAmount, false, );

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
    public PendingWithdraw requestWithdraw(long userId, @NonNull BigDecimal withdrawAmount, boolean isWithdrawFromProfit, String remarks) {
        UserInfo userInfo = userApi.getUserById(userId);
        BigDecimal walletBalance = userInfo.getWalletBalance();
        BigDecimal profitBalance = userInfo.getProfitWallet();
        String walletAddress = userInfo.getWalletAddress();
        String rankCode = userInfo.getRankCode();
        WithdrawRule withdrawRule = withdrawRuleConfigCache.findByRankCode(rankCode);

        // 1️⃣ Pre-calculate max withdraw and service charge
        //BigDecimal maxWithdrawAllowed  = calculateMaxWithdrawAllowed(walletBalance, withdrawRule);
        BigDecimal appliedServiceCharge = calculateServiceCharge(withdrawAmount, withdrawConfig);
        //BigDecimal totalDeduction = withdrawAmount.add(appliedServiceCharge);

        // 2️⃣ Check for existing pending withdraw
        boolean hasPendingWithdraw = pendingWithdrawRepository.existsByUserIdAndStatus(userId, PendingWithdraw.WithdrawStatus.PENDING);
        if (hasPendingWithdraw) {
            throw new TransactionException("There is already a pending withdraw request for this user.");
        }

        // Validate rank rules (members & referrals)
        validateWithdrawRules(
                userId,
                rankCode,
                withdrawAmount,
                isWithdrawFromProfit,
                isWithdrawFromProfit ? profitBalance : walletBalance
        );

        // 6️⃣ Deduct immediately from wallet
        Transaction transaction = walletService.updateWalletBalance(
                userId,
                withdrawAmount,        // deduct only withdraw amount from wallet
                appliedServiceCharge,  // track service charge for system accounting
                isWithdrawFromProfit? TransactionType.WITHDRAWAL_FROM_PROFIT : TransactionType.WITHDRAWAL,
                "withdraw-service",
                false,
                TransactionRemarks.WITHDRAW_REQUESTED,
                null,
                isWithdrawFromProfit
        );

        // 7️⃣ Create PendingWithdraw record
        PendingWithdraw withdraw = new PendingWithdraw();
        withdraw.setTxnRefId(transaction.getTxnRefId());
        withdraw.setUserId(userId);
        withdraw.setAmount(withdrawAmount);
        withdraw.setServiceCharge(appliedServiceCharge);
        withdraw.setWalletAddress(walletAddress);
        withdraw.setRankCode(rankCode); // <-- store rankCode for attempt tracking
        withdraw.setProfitWallet(isWithdrawFromProfit);
        withdraw.setStatus(PendingWithdraw.WithdrawStatus.PENDING);
        withdraw.setRemarks(remarks);

        // 8️⃣ Publish notification
        withdrawNotificationService.publishWithdrawRequestReceived(userInfo, withdraw);

        return pendingWithdrawRepository.save(withdraw);
    }

    private BigDecimal calculateServiceCharge(BigDecimal withdrawAmount, WithdrawConfigProperty config) {
        log.debug("Calculating service charge for withdrawAmount={}", withdrawAmount);

        BigDecimal appliedServiceCharge;
        BigDecimal threshold = config.getServiceChargeThreshold();
        BigDecimal fixedCharge = config.getServiceChargeFixed();
        BigDecimal percentage = config.getServiceChargePercentage();

        if (withdrawAmount.compareTo(threshold) < 0) {
            log.debug("Withdraw amount {} is below threshold {}. Applying fixed service charge: {}", withdrawAmount, threshold, fixedCharge);
            appliedServiceCharge = config.getServiceChargeFixed();
        } else {
            appliedServiceCharge = withdrawAmount.multiply(config.getServiceChargePercentage()).setScale(2, RoundingMode.HALF_UP);
            log.debug("Withdraw amount {} is above or equal to threshold {}. Applying percentage-based charge: {}% → {}", withdrawAmount, threshold, percentage.multiply(BigDecimal.valueOf(100)), appliedServiceCharge);
        }
        log.info("Final service charge for withdrawAmount {} is {}", withdrawAmount, appliedServiceCharge);
        return appliedServiceCharge;
    }


    private BigDecimal calculateMaxWithdrawAllowed(BigDecimal walletBalance, WithdrawRule rule) {
        /*
        // Get withdraw percentage from config map
        BigDecimal withdrawPercentage = withdrawConfig.getWithdrawLimitByRankMap()
                .getOrDefault(rankCode, BigDecimal.ONE); // default 100% if rank not found

        // Max withdraw = walletBalance * percentage
        return walletBalance.multiply(withdrawPercentage);
         */

        /*WithdrawRule rule = withdrawRuleConfigCache.findByRankCode(rankCode);
        if (rule.getMaxWithdrawAmount() != null && rule.getMaxWithdrawAmount().compareTo(BigDecimal.ZERO) > 0) {
            return walletBalance.min(rule.getMaxWithdrawAmount());
        }
        return walletBalance; // fallback: full balance allowed*/

        BigDecimal allowedByPercentage = walletBalance;
        if (rule.getWithdrawLimitFromWalletInPercentage() != null) {
            allowedByPercentage = walletBalance
                    .multiply(rule.getWithdrawLimitFromWalletInPercentage())
                    .divide(BigDecimal.valueOf(100), RoundingMode.DOWN);
        }

        // if maxWithdrawFromWallet is set, take the minimum
        BigDecimal allowedByAbsolute = rule.getMaxWithdrawFromWallet() != null
                && rule.getMaxWithdrawFromWallet().compareTo(BigDecimal.ZERO) > 0
                ? rule.getMaxWithdrawFromWallet()
                : walletBalance;

        return allowedByPercentage.min(allowedByAbsolute);
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
                null,
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

    private void validateWithdrawAttempts(Long userId, String rankCode, boolean isWithdrawFromProfit) {
        BigDecimal withdrawPercentage = withdrawConfig.getWithdrawLimitByRankMap().getOrDefault(rankCode, BigDecimal.ONE);

        // If rank is allowed full withdraw (100%), skip attempt restriction
        if (withdrawPercentage.compareTo(BigDecimal.ONE) == 0) {
            return; // unlimited
        }

        // Otherwise restrict attempts
        int usedAttempts = pendingWithdrawRepository.countByUserIdAndRankCodeAndIsProfitWalletAndStatus(
                userId,
                rankCode,
                isWithdrawFromProfit,
                PendingWithdraw.WithdrawStatus.APPROVED
        );

        if (usedAttempts >= 1) {  // 🔄 configurable if needed
            throw new TransactionException("You have already used your maximum withdraw limit for rank " + rankCode);
        }
    }

    private void validateWithdrawRules(
            long userId,
            String rankCode,
            BigDecimal withdrawAmount,
            boolean isWithdrawFromProfit,
            BigDecimal balance
    ) {
        log.debug("Validating withdraw rules for userId={}, rankCode={}, amount={}, fromProfit={}, balance={}",
                userId, rankCode, withdrawAmount, isWithdrawFromProfit, balance);

        WithdrawRule rule = withdrawRuleConfigCache.findByRankCode(rankCode);
        if (rule == null) {
            throw new TransactionException("No withdrawal rule configured for rank " + rankCode);
        }

        String walletLabel = isWithdrawFromProfit ? "profit wallet" : "main wallet";

        // 1️⃣ Basic balance check first
        if (withdrawAmount.compareTo(balance) > 0) {
            log.warn("Insufficient {} balance for userId={}. Requested={}, Available={}", walletLabel, userId, withdrawAmount, balance);
            throw new TransactionException("Insufficient " + walletLabel + " balance.");
        }

        // Pick rule values
        BigDecimal percentageLimit = isWithdrawFromProfit
                ? rule.getWithdrawLimitFromProfitWalletInPercentage()
                : rule.getWithdrawLimitFromWalletInPercentage();
        BigDecimal absoluteLimit = isWithdrawFromProfit
                ? rule.getMaxWithdrawFromProfitWallet()
                : rule.getMaxWithdrawFromWallet();

        // 2️⃣ Percentage-based withdraw limit
        if (percentageLimit != null) {
            if (percentageLimit.compareTo(BigDecimal.ZERO) == 0) {
                log.warn("Withdraw not allowed from {} for userId={} (0% rule)", walletLabel, userId);
                throw new TransactionException("Withdraw from " + walletLabel + " is not allowed for rank " + rankCode);
            }

            if (percentageLimit.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal allowedByPercentage = balance
                        .multiply(percentageLimit)
                        .divide(BigDecimal.valueOf(100), RoundingMode.DOWN);

                log.debug("Percentage-based limit for userId={}, wallet={}, allowed={}, percentageLimit={}%",
                        userId, walletLabel, allowedByPercentage, percentageLimit);

                if (withdrawAmount.compareTo(allowedByPercentage) > 0) {
                    throw new TransactionException("You can withdraw from " + walletLabel + " up to "
                            + allowedByPercentage + " (" + percentageLimit + "% of balance) for rank " + rankCode);
                }
            }
        }

        // 3️⃣ Absolute max per request
        if (absoluteLimit != null && absoluteLimit.compareTo(BigDecimal.ZERO) > 0
                && withdrawAmount.compareTo(absoluteLimit) > 0) {
            log.warn("Withdraw amount exceeds absolute limit for userId={}. Requested={}, MaxAllowed={}", userId, withdrawAmount, absoluteLimit);
            throw new TransactionException("Maximum withdraw from " + walletLabel
                    + " per request is " + absoluteLimit + " for rank " + rankCode);
        }

        // 4️⃣ Daily withdraw attempts
        if (rule.getDailyWithdrawLimit() > 0) {
            int todayAttempts = pendingWithdrawRepository.countByUserIdAndRankCodeAndStatusAndCreatedAtBetween(
                    userId,
                    rankCode,
                    PendingWithdraw.WithdrawStatus.APPROVED,
                    LocalDate.now().atStartOfDay(),
                    LocalDate.now().plusDays(1).atStartOfDay()
            );
            log.debug("Daily withdraw attempts for userId={}, rankCode={}: {}/{}", userId, rankCode, todayAttempts, rule.getDailyWithdrawLimit());

            if (todayAttempts >= rule.getDailyWithdrawLimit()) {
                log.warn("UserId={} has reached daily withdraw limit for rankCode={}", userId, rankCode);
                throw new TransactionException("You have reached your daily withdraw limit ("
                        + rule.getDailyWithdrawLimit() + ")");
            }
        }

        // 5️⃣ Total lifetime withdraw attempts
        int totalApproved = pendingWithdrawRepository.countByUserIdAndRankCodeAndIsProfitWalletAndStatus(
                userId,
                rankCode,
                isWithdrawFromProfit,
                PendingWithdraw.WithdrawStatus.APPROVED
        );

        int lifetimeLimit = isWithdrawFromProfit
                ? rule.getTotalProfitWithdrawLimit()
                : rule.getTotalWalletWithdrawLimit();
        String walletType = isWithdrawFromProfit ? "profit wallet" : "main wallet";

        if (lifetimeLimit > 0) {
            log.debug("Total approved {} withdraws for userId={}, rankCode={}: {}/{}", walletType, userId, rankCode, totalApproved, lifetimeLimit);

            if (totalApproved >= lifetimeLimit) {
                log.warn("UserId={} has exceeded total {} withdraw limit for rankCode={}", userId, walletType, rankCode);
                throw new TransactionException("You have already used your total " + walletType + " withdraw limit for rank " + rankCode);
            }
        }
    }


}
