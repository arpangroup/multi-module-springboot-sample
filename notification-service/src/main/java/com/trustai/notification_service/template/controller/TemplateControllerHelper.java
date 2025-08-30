package com.trustai.notification_service.template.controller;

import com.trustai.notification_service.template.service.TemplateService;
import com.trustai.notification_service.template.service.TemplateServiceRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class TemplateControllerHelper {
    private final TemplateServiceRegistry registry;

    public ResponseEntity<?> getTemplates(String type, int page, int size) {
        TemplateService<?> service = registry.getService(type);
        return ResponseEntity.ok(service.getTemplates(page, size));
    }

    public ResponseEntity<?> getTemplateById(String type, Long id) {
        TemplateService<?> service = registry.getService(type);
        return ResponseEntity.ok(service.getTemplateById(id));
    }

    public ResponseEntity<?> updateTemplate(String type, Long id, Map<String, String> updates) {
        TemplateService<?> service = registry.getService(type);
        return ResponseEntity.ok(service.updateTemplate(id, updates));
    }

    public ResponseEntity<?> getTemplateByCode(String type, String code) {
        TemplateService<?> service = registry.getService(type);
        return ResponseEntity.ok(service.getByCode(code));
    }
}
