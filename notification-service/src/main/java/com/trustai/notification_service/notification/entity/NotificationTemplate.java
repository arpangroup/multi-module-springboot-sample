package com.trustai.notification_service.notification.entity;

import com.trustai.common.enums.NotificationChannel;
import com.trustai.notification_service.notification.enums.NotificationCode;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_template")
@Data
@NoArgsConstructor
public class NotificationTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code; // unique template code

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    private NotificationChannel notificationChannel = NotificationChannel.EMAIL;

    @Column(nullable = false)
    private String subject;

    @Lob
    private String content; // use {{var}} placeholders

    public NotificationTemplate(String code, NotificationChannel notificationChannel, String subject, String content) {
        this.code = code;
        this.notificationChannel = notificationChannel;
        this.subject = subject;
        this.content = content;
    }

    public static NotificationTemplate email(NotificationCode code, String subject, String content) {
        return new NotificationTemplate(code.name(), NotificationChannel.EMAIL, subject, content);
    }

    public static NotificationTemplate sms(NotificationCode code, String content) {
        return new NotificationTemplate(code.name(), NotificationChannel.SMS, "", content);
    }

    public static NotificationTemplate push(NotificationCode code, String subject, String content) {
        return new NotificationTemplate(code.name(), NotificationChannel.PUSH, subject, content);
    }
}
