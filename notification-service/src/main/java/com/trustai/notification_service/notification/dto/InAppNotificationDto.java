package com.trustai.notification_service.notification.dto;

import com.trustai.notification_service.notification.entity.InAppNotification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InAppNotificationDto {
    private Long id;
    private String title;
    private String message;
    private boolean viewed;
    private LocalDateTime createdAt;
}