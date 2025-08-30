package com.trustai.notification_service.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPublishRequest {
    private String title;
    private String message;
    private boolean global;
    private List<Long> userIds;
}
