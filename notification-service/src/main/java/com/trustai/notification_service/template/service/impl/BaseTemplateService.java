package com.trustai.notification_service.template.service.impl;

import com.trustai.notification_service.template.exception.TemplateNotFoundException;
import com.trustai.notification_service.template.entity.Template;
import com.trustai.notification_service.template.repository.CodeBasedTemplateRepository;
import com.trustai.notification_service.template.service.TemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Map;
import java.util.Optional;

@Slf4j
public abstract class BaseTemplateService<T extends Template, R extends CodeBasedTemplateRepository<T>> implements TemplateService<T> {
    protected final R repository;

    protected BaseTemplateService(R repository) {
        this.repository = repository;
    }

    @Override
    public Page<T> getTemplates(Integer page, Integer size) {
        log.info("Fetching templates with pagination - page: {}, size: {}", page, size);
        Page<T> templates = repository.findAll(PageRequest.of(page, size));
        log.info("Fetched {} templates", templates.getNumberOfElements());
        return templates;
    }

    @Override
    public T getTemplateById(Long id) {
        log.info("Fetching template by ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Template not found with ID: {}", id);
                    return new TemplateNotFoundException(id);
                });
    }

    @Override
    public Optional<T> findByCode(String code) {
        log.info("Finding template by code: {}", code);
        Optional<T> result = repository.findByCode(code);
        if (result.isPresent()) {
            log.info("Template found for code: {}", code);
        } else {
            log.warn("No template found for code: {}", code);
        }
        return result;
    }

    @Override
    public T getByCode(String code) {
        log.info("Getting template by code (with exception if not found): {}", code);
        return repository.findByCode(code)
                .orElseThrow(() -> {
                    log.error("Template not found with code: {}", code);
                    return new TemplateNotFoundException(code);
                });
    };


    @Override
    public T updateTemplate(Long id, Map<String, String> updates) {
        log.info("Updating template with ID: {} and updates: {}", id, updates);
        T template = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Template not found for update with ID: {}", id);
                    return new TemplateNotFoundException(id);
                });
        template.updateFields(updates);
        T updated = repository.save(template);
        log.info("Template with ID: {} updated successfully", id);
        return updated;
    }
}
