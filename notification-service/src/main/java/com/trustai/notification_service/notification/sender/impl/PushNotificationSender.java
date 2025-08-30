/*
package com.trustai.notification_service.notification.sender.impl;

import com.trustai.notification_service.dto.NotificationRequest;
import com.trustai.common.enums.NotificationChannel;
import com.trustai.notification_service.notification.sender.NotificationSender;
import com.trustai.notification_service.notification.service.impl.FirebaseMessagingService;
import com.trustai.notification_service.template.service.impl.PushTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PushNotificationSender implements NotificationSender {
    private final FirebaseMessagingService firebaseMessagingService; // From Firebase Admin SDK

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.PUSH;
    }

    @Override
    public void send(NotificationRequest request) {
        firebaseMessagingService.sendNotification(
                request.getRecipient(),
                request.getTitle(),
                request.getMessage()
        );
    }
}
*/
