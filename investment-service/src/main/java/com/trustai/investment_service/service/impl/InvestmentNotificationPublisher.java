package com.trustai.investment_service.service.impl;

import com.trustai.common.dto.NotificationRequest;
import com.trustai.common.dto.UserInfo;
import com.trustai.common.event.NotificationEvent;
import com.trustai.investment_service.entity.InvestmentSchema;
import com.trustai.investment_service.entity.UserInvestment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvestmentNotificationPublisher {

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
    public void sendInvestmentSuccessNotification(UserInfo userInfo, UserInvestment investment) {
        InvestmentSchema schema = investment.getSchema();
        String currency = schema.getCurrency().name();
        String username = userInfo.getFirstname() != null ? userInfo.getFirstname() : "User";

        String title = "Stake Investment Confirmed Successfully";
        String message = String.format(
                "Dear %s,\n\n" +
                        "We're pleased to inform you that your stake investment in **%s** has been successfully confirmed.\n\n" +
                        "**Details:**\n" +
                        "- Investment Amount: %s %s\n" +
                        "- Profit Rate: %s%% per %s\n" +
                        "- Next Payout: %s\n" +
                        "- Maturity Date: %s\n\n" +
                        "You can monitor your investment performance in your dashboard.\n\n" +
                        "Thank you for investing with us!\n\n" +
                        "Best regards,\nTrustAI Team",
                username,
                schema.getName(),
                investment.getInvestedAmount().setScale(2, RoundingMode.HALF_UP).toPlainString(),
                currency,
                schema.getReturnRate().setScale(2, RoundingMode.HALF_UP).toPlainString(),
                schema.getPayoutMode().name(),
                formatDate(investment.getNextPayoutAt()),
                formatDate(investment.getMaturityAt())
        );

        sendNotification(userInfo, title, message);
    }

    @Async
    public void sendInvestmentMatureNotification(UserInfo userInfo, UserInvestment investment) {
        InvestmentSchema schema = investment.getSchema();
        String currency = schema.getCurrency().name();
        String username = userInfo.getFirstname() != null ? userInfo.getFirstname() : "User";

        String title = "Your Stake Investment Has Matured";
        String message = String.format(
                "Dear %s,\n\n" +
                        "Congratulations! Your stake investment in **%s** has successfully matured.\n\n" +
                        "**Final Summary:**\n" +
                        "- Total Invested: %s %s\n" +
                        "- Total Earnings: %s %s\n" +
                        "- Capital Returned: %s %s\n" +
                        "- Investment Duration: %s to %s\n\n" +
                        "Your returns have been credited to your wallet. You can view the full breakdown in your investment history.\n\n" +
                        "We appreciate your trust and look forward to your continued investment with us.\n\n" +
                        "Warm regards,\nTrustAI Team",
                username,
                schema.getName(),
                investment.getInvestedAmount().setScale(2, RoundingMode.HALF_UP).toPlainString(),
                currency,
                investment.getFinalReturnAmount() != null
                        ? investment.getFinalReturnAmount().setScale(2, RoundingMode.HALF_UP).toPlainString()
                        : "0.00",
                currency,
                investment.getCapitalAmountReturned() != null
                        ? investment.getCapitalAmountReturned().setScale(2, RoundingMode.HALF_UP).toPlainString()
                        : "0.00",
                currency,
                formatDate(investment.getSubscribedAt()),
                formatDate(investment.getMaturityAt())
        );

        sendNotification(userInfo, title, message);
    }

    private String formatDate(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        return dateTime.format(formatter);
    }
}
