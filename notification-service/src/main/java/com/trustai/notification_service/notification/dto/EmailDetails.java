package com.trustai.notification_service.notification.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailDetails {
    private String recipient;
    private String msgBody;
    private String subject;
    private String attachment;

    public EmailDetails(String to, String subject, String text) {
        this.recipient = to;
        this.subject = subject;
        this.msgBody = text;
    }

    public EmailDetails(String to, String subject, String text, String attachment) {
        this(to, subject, text);
        this.attachment = attachment;
    }
}
