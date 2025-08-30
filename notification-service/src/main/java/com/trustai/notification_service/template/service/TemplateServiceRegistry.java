package com.trustai.notification_service.template.service;

import com.trustai.notification_service.template.enums.TemplateType;
import com.trustai.notification_service.template.entity.Template;
import com.trustai.notification_service.template.service.impl.EmailTemplateService;
import com.trustai.notification_service.template.service.impl.PushTemplateService;
import com.trustai.notification_service.template.service.impl.SmsTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Helps in resolving services dynamically by type. Useful.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TemplateServiceRegistry {
    private final Map<TemplateType, TemplateService<? extends Template>> serviceMap; // If more channels are expected in the future:
    private final EmailTemplateService emailService;
    private final SmsTemplateService smsService;
    private final PushTemplateService pushService;

    public TemplateService<?> getService(TemplateType type) {
        log.info("Resolving template service for type: {}", type);
        TemplateService<?> service = serviceMap.get(type);
        if (service == null) {
            log.error("No template service found for type: {}", type);
            throw new IllegalArgumentException("No template service found for type: " + type);
        }
        log.info("Resolved service: {} for type: {}", service.getClass().getSimpleName(), type);
        return service;
    }

    public TemplateService<?> getService(String type) {
        log.info("Resolving template service for string type: {}", type);
        TemplateService<?> service;
        switch (type.toLowerCase()) {
            case "email" -> service = emailService;
            case "sms" -> service = smsService;
            case "push" -> service = pushService;
            default -> {
                log.error("Unsupported template type: {}", type);
                throw new IllegalArgumentException("Unsupported template type: " + type);
            }
        }
        log.info("Resolved service: {} for string type: {}", service.getClass().getSimpleName(), type);
        return service;
    }
}
