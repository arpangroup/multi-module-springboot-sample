package com.trustai.transaction_service.service;

import com.trustai.common.dto.NotificationRequest;
import com.trustai.common.dto.UserInfo;
import com.trustai.common.event.NotificationEvent;
import com.trustai.transaction_service.entity.PendingWithdraw;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WithdrawNotificationService {
    private final ApplicationEventPublisher publisher;

    private void sendNotification(UserInfo userInfo, String title, String message) {
        // In-App
        log.info("📢 Publishing InApp Notification | userId={}, title='{}'", userInfo.getId(), title);
        NotificationRequest inAppRequest = NotificationRequest.forInApp(
                String.valueOf(userInfo.getId()),
                title,
                message
        );
        publisher.publishEvent(new NotificationEvent(this, inAppRequest));

        // Email
        log.info("📧 Publishing Email Notification | email={}, subject='{}'", userInfo.getEmail(), title);
        NotificationRequest emailRequest = NotificationRequest.forEmail(
                userInfo.getEmail(),
                title,
                message
        );
        publisher.publishEvent(new NotificationEvent(this, emailRequest));
    }


    @Async
    public void publishWithdrawRequestReceived(UserInfo userInfo, PendingWithdraw withdraw) {
        String title = "Withdrawal Request Received";
        String message = String.format(
                "Hello %s, we have received your withdrawal request for %s. Our team will review and process it shortly. Thank you for choosing TrustAI!",
                userInfo.getFirstname() != null ? userInfo.getFirstname() : "User",
                withdraw.getAmount().toPlainString()
        );

        sendNotification(userInfo, title, message);
    }

    @Async
    public void publishWithdrawApproved(UserInfo userInfo, PendingWithdraw withdraw) {
        String title = "Your Withdrawal Request Has Been Approved";
        String message = String.format(
                "Hello %s, your withdrawal request of %s has been successfully approved and is being processed. Thank you for using TrustAI!",
                userInfo.getFirstname() != null ? userInfo.getFirstname() : "User",
                withdraw.getAmount().toPlainString()
        );

        sendNotification(userInfo, title, message);
    }

    @Async
    public void publishWithdrawRejected(UserInfo userInfo, PendingWithdraw withdraw) {
        String title = "Your Withdrawal Request Has Been Rejected";
        String message = String.format(
                "Hello %s, unfortunately, your withdrawal request of %s has been rejected. The deducted amount has been refunded to your wallet. Please check your account or contact support for more details.",
                userInfo.getFirstname() != null ? userInfo.getFirstname() : "User",
                withdraw.getAmount().toPlainString()
        );

        sendNotification(userInfo, title, message);
    }
}
