package com.trustai.notification_service.notification.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class EmailRequest {
    private String recipient;
    private String subject;
    private String message;
    private boolean isSendToAll;
}
