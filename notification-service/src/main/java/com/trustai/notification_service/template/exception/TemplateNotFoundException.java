package com.trustai.notification_service.template.exception;

public class TemplateNotFoundException extends RuntimeException{

    public TemplateNotFoundException(String code) {
        super("Template code=" + code + " not found");
    }

    public TemplateNotFoundException(Long id) {
        super("Template Id=" + id + " not found");
    }
}
