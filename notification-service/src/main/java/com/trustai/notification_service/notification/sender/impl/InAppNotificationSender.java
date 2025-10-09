package com.trustai.notification_service.notification.sender.impl;

import com.trustai.common.api.UserApi;
import com.trustai.common.dto.NotificationRequest;
import com.trustai.notification_service.notification.entity.InAppNotification;
import com.trustai.common.enums.NotificationChannel;
import com.trustai.notification_service.notification.mapper.InAppNotificationMapper;
import com.trustai.notification_service.notification.repository.InAppNotificationRepository;
import com.trustai.notification_service.notification.sender.NotificationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InAppNotificationSender implements NotificationSender {
    private final InAppNotificationRepository inAppNotificationRepository;
    private final InAppNotificationMapper mapper;
    private final UserApi userApi;

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.IN_APP;
    }

    @Override
    public void send(NotificationRequest request) {
        log.info("Sending in-app notification: {}........", request);

        List<Long> targetUserIds = request.isSendToAll()
                ? userApi.findAllActiveUserIds() // method to implement
                : List.of(Long.parseLong(request.getRecipient()));


        log.info("Target UserIds: {}........", targetUserIds);
        List<InAppNotification> notifications = targetUserIds.stream().map(uid -> mapper.toEntity(request, uid)).toList();
        inAppNotificationRepository.saveAll(notifications);
    }
}
