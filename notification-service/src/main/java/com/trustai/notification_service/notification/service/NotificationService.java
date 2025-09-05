package com.trustai.notification_service.notification.service;

import com.trustai.notification_service.notification.sender.NotificationSenderFactory;
import com.trustai.common.dto.NotificationRequest;
import com.trustai.notification_service.template.render.TemplateRenderer;
import com.trustai.notification_service.template.service.impl.EmailTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationSenderFactory senderFactory;
    private final TemplateRenderer templateRenderer;
    private final EmailTemplateService emailTemplateService;

    @Async
    public void send(NotificationRequest request) {
        log.info("Preparing to send notification with request: {}", request);

        if (request.getTemplateCode() != null) {
            log.info("Template code provided: {}. Fetching template...", request.getTemplateCode());
            var template = emailTemplateService.getByCode(request.getTemplateCode());

            log.debug("Rendering message body with properties: {}", request.getProperties());
            String renderedMessage = templateRenderer.render(template.getMessageBody(), request.getProperties());
            request.setMessage(renderedMessage);
            request.setSubject(template.getSubject());
            log.info("Template rendering complete. Subject: '{}'", request.getSubject());
        } else {
            log.info("No template code provided. Using raw message and subject.");
        }

        request.getChannels().forEach(channel -> {
            log.info("Sending notification via channel: {}", channel);
            var sender = senderFactory.getSender(channel);
            if (sender != null) {
                sender.send(request);
                log.info("Notification sent via channel: {}", channel);
            } else {
                log.warn("No sender found for channel: {}", channel);
            }
        });
        log.info("Notification processing completed for request: {}", request);
    }
}
