package com.trustai.notification_service.template.repository;

import com.trustai.notification_service.notification.entity.NotificationTemplate;
import com.trustai.common.enums.NotificationChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<NotificationTemplate, Long> {
    Optional<NotificationTemplate> findByCodeAndNotificationChannel(String code, NotificationChannel notificationChannel);

    boolean existsByCode(String code);
}
