package com.trustai.notification_service.notification.sender.impl;

import com.trustai.common.dto.NotificationRequest;
import com.trustai.common.enums.NotificationChannel;
import com.trustai.notification_service.notification.sender.NotificationSender;
import com.trustai.notification_service.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationSender implements NotificationSender {
    private final EmailService emailService;

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public void send(NotificationRequest request) {
        emailService.sendMail(
                request.getRecipient(),
                request.getSubject(),
                request.getMessage()
        );
    }
}
