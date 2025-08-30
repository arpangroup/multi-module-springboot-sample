package com.trustai.notification_service.template.service.impl;

import com.trustai.notification_service.template.entity.EmailTemplate;
import com.trustai.notification_service.template.repository.EmailTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailTemplateService extends BaseTemplateService<EmailTemplate, EmailTemplateRepository> {

    protected EmailTemplateService(EmailTemplateRepository repository) {
        super(repository);
    }
}
