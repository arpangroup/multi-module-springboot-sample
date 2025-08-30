package com.trustai.notification_service.notification.sender;

import com.trustai.common.dto.NotificationRequest;
import com.trustai.common.enums.NotificationChannel;

public interface NotificationSender {
    NotificationChannel getChannel(); // Returns the type it supports
    void send(NotificationRequest request);
}
