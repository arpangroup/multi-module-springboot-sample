package com.trustai.notification_service.notification.sender.impl;

import com.trustai.common.dto.NotificationRequest;
import com.trustai.common.enums.NotificationChannel;
import com.trustai.notification_service.notification.sender.NotificationSender;
import com.trustai.notification_service.notification.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmsNotificationSender implements NotificationSender {
    private final SmsService smsService;

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.SMS;
    }

    @Override
    public void send(NotificationRequest request) {
        smsService.sendSms(
                request.getRecipient(),
                request.getMessage()
        );
    }
}
