package com.trustai.notification_service.template.enums;

public enum TemplateType {
    EMAIL,
    SMS,
    PUSH,
    IN_APP,
    WHATSApp;

    public static TemplateType fromString(String value) {
        value = value.toUpperCase();
        for (TemplateType type : TemplateType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown TemplateType: " + value);
    }
}
