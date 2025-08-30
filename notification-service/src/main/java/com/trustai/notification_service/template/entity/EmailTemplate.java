package com.trustai.notification_service.template.entity;

import com.trustai.common.utils.FieldUpdateUtil;
import com.trustai.notification_service.notification.enums.NotificationCode;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

@Entity
@Table(name = "email_templates")
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class EmailTemplate implements Template {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(unique = true, nullable = false)
    private String code; // templateName

    @Column(nullable = false)
    private String subject;

    private String banner;
    private String title;
    private String salutation;
    @Lob
    private String messageBody;

    private String buttonLevel;
    private String buttonLink;

    private boolean enableFooterStatus;
    private String footerBody;
    private boolean enableFooterBottom;
    private String bottomTitle;
    private String bottomBody;

    private String templateFor;
    private boolean templateActive;

    public EmailTemplate(String code, String subject) {
        this.code = code;
        this.subject = subject;
    }

    public EmailTemplate(NotificationCode code, String subject) {
        this(code.name(), subject);
    }

    /*@Override
    public void updateFields(Map<String, String> updates) {
        updates.forEach((key, value) -> {
            switch (key) {
                case "code" -> this.setCode(value);
                case "subject" -> this.setSubject(value);
                case "banner" -> this.setBanner(value);
                case "title" -> this.setTitle(value);
                case "salutation" -> this.setSalutation(value);
                case "messageBody" -> this.setMessageBody(value);
                case "buttonLevel" -> this.setButtonLevel(value);
                case "buttonLink" -> this.setButtonLink(value);
                case "enableFooterStatus" -> this.setEnableFooterStatus(Boolean.parseBoolean(value));
                case "footerBody" -> this.setFooterBody(value);
                case "enableFooterBottom" -> this.setEnableFooterBottom(Boolean.parseBoolean(value));
                case "bottomTitle" -> this.setBottomTitle(value);
                case "bottomBody" -> this.setBottomBody(value);
                case "templateFor" -> this.setTemplateFor(value);
                case "templateActive" -> this.setTemplateActive(Boolean.parseBoolean(value));
                // ...
                default -> throw new IllegalArgumentException("Invalid field: " + key);
            }
        });
    }*/

    public void updateFields(Map<String, String> updates) {
        FieldUpdateUtil.updateFields(this, updates);
    }
}
