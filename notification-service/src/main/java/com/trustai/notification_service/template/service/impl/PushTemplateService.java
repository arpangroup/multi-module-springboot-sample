package com.trustai.notification_service.template.service.impl;

import com.trustai.notification_service.template.entity.PushNotificationTemplate;
import com.trustai.notification_service.template.repository.PushNotificationTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PushTemplateService extends BaseTemplateService<PushNotificationTemplate, PushNotificationTemplateRepository> {

    protected PushTemplateService(PushNotificationTemplateRepository repository) {
        super(repository);
    }

}
