package com.trustai.notification_service.template.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
@Slf4j
public class TemplateController {
    private final TemplateControllerHelper helper;

    @GetMapping("/{type}")
    public ResponseEntity<?> getTemplates(
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Fetching templates of type '{}', page: {}, size: {}", type, page, size);
        ResponseEntity<?> response = helper.getTemplates(type, page, size);
        log.info("Templates fetched for type '{}'", type);
        return response;
    }

    @GetMapping("/{type}/{id}")
    public ResponseEntity<?> getTemplateById(@PathVariable String type, @PathVariable Long id) {
        log.info("Fetching template by ID. Type: '{}', ID: {}", type, id);
        ResponseEntity<?> response = helper.getTemplateById(type, id);
        log.info("Fetched template with ID: {} for type '{}'", id, type);
        return response;
    }

    @PutMapping("/{type}/{id}")
    public ResponseEntity<?> updateTemplate(
            @PathVariable String type,
            @PathVariable Long id,
            @RequestBody Map<String, String> updates
    ) {
        log.info("Updating template. Type: '{}', ID: {}, Updates: {}", type, id, updates);
        ResponseEntity<?> response = helper.updateTemplate(type, id, updates);
        log.info("Template updated. ID: {}, Type: '{}'", id, type);
        return response;
    }

    @GetMapping("/{type}/code/{code}")
    public ResponseEntity<?> getTemplateByCode(@PathVariable String type, @PathVariable String code) {
        log.info("Fetching template by code. Type: '{}', Code: {}", type, code);
        ResponseEntity<?> response = helper.getTemplateByCode(type, code);
        log.info("Fetched template for type '{}' with code: {}", type, code);
        return response;
    }
}
