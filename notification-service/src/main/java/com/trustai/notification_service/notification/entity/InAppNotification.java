package com.trustai.notification_service.notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "in_app_notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InAppNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;            // Recipient user ID

    @Column(length = 200)
    private String title;           // Optional: title for UI display

    @Column(nullable = false, length = 2000)
    private String message;          // Main message body

    private String link;             // Optional: deep link or reference to related resource

    @Lob
    private String metadata;         // Optional: extra metadata in JSON format

    @Column(nullable = false)
    private boolean viewed = false;  // Whether the notification has been viewed/read

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime viewedAt;   // When it was viewed

    @Column(nullable = false)
    private boolean deleted = false;  // ✅ Soft delete flag

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
