package com.trustai.notification_service.template.service.impl;

import com.trustai.notification_service.template.entity.SmsTemplate;
import com.trustai.notification_service.template.repository.SmsTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SmsTemplateService extends BaseTemplateService<SmsTemplate, SmsTemplateRepository> {

    protected SmsTemplateService(SmsTemplateRepository repository) {
        super(repository);
    }
}
