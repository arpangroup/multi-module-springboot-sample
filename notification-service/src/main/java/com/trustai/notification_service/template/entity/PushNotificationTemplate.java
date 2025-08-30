package com.trustai.notification_service.template.entity;

import com.trustai.notification_service.notification.enums.NotificationCode;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

@Entity
@Table(name = "push_templates")
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class PushNotificationTemplate implements Template {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code; // templateName

    private String title;

    private String messageBody;

    private String templateFor;
    private boolean templateActive = true;

    public PushNotificationTemplate(String code) {
        this.code = code;
    }
    public PushNotificationTemplate(NotificationCode code) {
        this(code.name());
    }

    @Override
    public void updateFields(Map<String, String> updates) {
        updates.forEach((key, value) -> {
            switch (key) {
                case "code" -> this.setCode(value);
                case "title" -> this.setTitle(value);
                case "messageBody" -> this.setMessageBody(value);
                case "templateFor" -> this.setTemplateFor(value);
                case "templateActive" -> this.setTemplateActive(Boolean.parseBoolean(value));
                // ...
                default -> throw new IllegalArgumentException("Invalid field: " + key);
            }
        });
    }
}
