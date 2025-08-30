package com.trustai.notification_service.notification.strategy.impl;

import com.trustai.common.enums.NotificationChannel;
import com.trustai.notification_service.notification.service.EmailService;
import com.trustai.notification_service.notification.strategy.NotificationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationStrategy implements NotificationStrategy {
    private final EmailService emailService;

    @Override
    public NotificationChannel getType() { return NotificationChannel.EMAIL; }

    @Override
    public void send(String recipient, String subject, String content) {
        log.debug("Preparing to send email to {}", recipient);
        System.out.printf("[EMAIL] To: %s | Subject: %s | Body: %s\n", recipient, subject, content);
        try {
            emailService.sendMail(recipient, subject, content);
            log.info("Email sent to {} with subject '{}'", recipient, subject);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", recipient, e.getMessage(), e);
            throw e;
        }
    }
}
