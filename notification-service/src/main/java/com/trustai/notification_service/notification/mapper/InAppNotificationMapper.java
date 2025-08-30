package com.trustai.notification_service.notification.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustai.notification_service.notification.dto.InAppNotificationDto;
import com.trustai.common.dto.NotificationRequest;
import com.trustai.notification_service.notification.entity.InAppNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class InAppNotificationMapper {
    @Autowired
    private ObjectMapper objectMapper;

    public InAppNotification toEntity(NotificationRequest request, Long userId) {
        return InAppNotification.builder()
                .userId(userId)
                .title(request.getTitle())
                .message(request.getMessage())
                .link(null) // Populate if applicable
                .metadata(convertMetadataToJson(request.getMetadata()))
                .viewed(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public InAppNotificationDto toDto(InAppNotification entity) {
        return new InAppNotificationDto(
                entity.getId(),
                entity.getTitle(),
                entity.getMessage(),
                entity.isViewed(),
                entity.getCreatedAt()
        );
    }

    private String convertMetadataToJson(Map<String, Object> metadata) {
        if (metadata == null || metadata.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (JsonProcessingException e) {
            // Handle error appropriately, for now just return null
            return null;
        }
    }
}
