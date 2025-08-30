package com.trustai.notification_service.template.service;

import com.trustai.notification_service.template.entity.Template;
import org.springframework.data.domain.Page;

import java.util.Map;
import java.util.Optional;

public interface TemplateService<T extends Template> {
    Page<T> getTemplates(Integer page, Integer size);
    T getTemplateById(Long id);
    T updateTemplate(Long id, Map<String, String> updates);
    Optional<T> findByCode(String code);
    T getByCode(String code);
}
