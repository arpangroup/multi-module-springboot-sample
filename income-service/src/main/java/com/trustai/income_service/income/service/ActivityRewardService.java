package com.trustai.income_service.income.service;

import com.trustai.common.api.UserApi;
import com.trustai.common.api.WalletApi;
import com.trustai.common.dto.NotificationRequest;
import com.trustai.common.dto.UserInfo;
import com.trustai.common.dto.WalletUpdateRequest;
import com.trustai.common.enums.IncomeType;
import com.trustai.common.enums.TransactionType;
import com.trustai.common.event.NotificationEvent;
import com.trustai.income_service.constant.Remarks;
import com.trustai.income_service.income.entity.IncomeHistory;
import com.trustai.income_service.income.repository.IncomeHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityRewardService {
    private final IncomeHistoryRepository incomeRepo;
    private final WalletApi walletApi;
    private final UserApi userApi;
    private final ApplicationEventPublisher publisher;

    public void applyActivityReward(Long userId, BigDecimal rewardAmount, String message) {
        String remarks = message == null || message.length() < 5 ? Remarks.ACTIVITY_REWARD : message;
        UserInfo userInfo = userApi.getUserById(userId);

        // 1. Add to income history
        log.info("Saving activity reward of {} for user: {}...........", rewardAmount, userId);
        IncomeHistory incomeHistory = IncomeHistory.builder()
                .userId(userId)
                .amount(rewardAmount)
                .incomeType(IncomeType.ACTIVITY)
                .sourceUserId(userId)
                .note(remarks)
                .build();
        incomeRepo.save(incomeHistory);
        log.info("Saved activity reward of {} for user {}", rewardAmount, userId);

        // 2. Update seller wallet
        log.info("Updating wallet balance for userId={} with rewardAmount={}", userId, rewardAmount);
        WalletUpdateRequest rewardRequest = new WalletUpdateRequest(
                rewardAmount,
                TransactionType.BONUS,
                true,
                "activity-reward",
                remarks,
                null
        );
        walletApi.updateWalletBalance(userId, rewardRequest);
        log.info("Wallet updated successfully for userId={}", userId);

        // 3. Publish notification
        publishRewardedNotification(userInfo);
    }

    @Async
    private void publishRewardedNotification(UserInfo userInfo) {
        String title = "Congratulations! You’ve earned an Activity Reward";
        String message = "Thank you for staying active on TrustAI. Your wallet has been credited with a reward for your recent activity. Keep up the great work!";

        // 1. Publish In-App Notification
        log.info("📢 Publishing InApp Notification | userId={}, title='{}'", userInfo.getId(), title);
        NotificationRequest inAppRequest = NotificationRequest.forInApp(
                String.valueOf(userInfo.getId()),
                title,
                message
        );
        publisher.publishEvent(new NotificationEvent(this, inAppRequest));


        // 2. Publish Email Notification
        log.info("📧 Publishing Email Notification | email={}, subject='{}'", userInfo.getEmail(), title);
        NotificationRequest emailRequest = NotificationRequest.forEmail(
                userInfo.getEmail(),
                title,
                message
        );
        publisher.publishEvent(new NotificationEvent(this, emailRequest));
    }
}
