package com.trustai.notification_service.notification.controller;

import com.trustai.common.event.NotificationEvent;
import com.trustai.notification_service.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {
    private final NotificationService notificationService;

    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        log.info("📩 Received NotificationEvent: {}", event.getRequest());
        notificationService.send(event.getRequest());
    }
}
