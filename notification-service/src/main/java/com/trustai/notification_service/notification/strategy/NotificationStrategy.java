package com.trustai.notification_service.notification.strategy;


import com.trustai.common.enums.NotificationChannel;

public interface NotificationStrategy {
    NotificationChannel getType();
    void send(String recipient, String subject, String content);
}
