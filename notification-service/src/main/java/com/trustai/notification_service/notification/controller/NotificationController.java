package com.trustai.notification_service.notification.controller;

import com.trustai.common.controller.BaseController;
import com.trustai.notification_service.notification.dto.InAppNotificationDto;
import com.trustai.common.dto.NotificationRequest;
import com.trustai.notification_service.notification.service.InAppNotificationService;
import com.trustai.notification_service.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController extends BaseController {
    private final NotificationService notificationService;
    private final InAppNotificationService inAppNotificationService;

    // 1️⃣ Send Notification
    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> sendNotification(@RequestBody NotificationRequest request) {
        log.info("Sending notification: {}", request);
        notificationService.send(request);
        log.info("Notification sent successfully");
        return ResponseEntity.ok().build();
    }

    // 2️⃣ Get paginated notifications
    @GetMapping
    public Page<InAppNotificationDto> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        Long currentUserId = getCurrentUserId();
        log.info("Fetching notifications for user: {}, page: {}, size: {}, sortBy: {}, sortDir: {}", currentUserId, page, size, sortBy, sortDir);

        Page<InAppNotificationDto> notifications = inAppNotificationService.getUserNotifications(currentUserId, page, size, sortBy, sortDir);
        log.info("Fetched {} notifications for user: {}", notifications.getNumberOfElements(), currentUserId);
        return notifications;
    }

    // 3️⃣ Mark single notification as viewed/read
    @PatchMapping("/{id}/view")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markAsViewed(@PathVariable Long id) {
        Long currentUserId = getCurrentUserId();
        log.info("Marking notification {} as viewed for user: {}", id, currentUserId);
        inAppNotificationService.markAsViewed(currentUserId, id);
        log.info("Notification {} marked as viewed", id);
        return ResponseEntity.noContent().build();
    }

    // 4️⃣ Mark multiple notifications as viewed
    @PatchMapping("/view")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markMultipleAsViewed(@RequestBody List<Long> ids) {
        Long currentUserId = getCurrentUserId();
        log.info("Marking multiple notifications as viewed for user: {}, IDs: {}", currentUserId, ids);
        inAppNotificationService.markMultipleAsViewed(currentUserId, ids);
        log.info("Marked notifications as viewed: {}", ids);
        return ResponseEntity.noContent().build();
    }

    // 5️⃣ Delete single notification
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        Long currentUserId = getCurrentUserId();
        log.info("Deleting notification {} for user: {}", id, currentUserId);
        inAppNotificationService.deleteNotification(currentUserId, id);
        log.info("Notification {} deleted", id);
        return ResponseEntity.noContent().build();
    }

    // 7️⃣ Delete multiple notifications
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteMultipleNotifications(@RequestBody List<Long> ids) {
        Long currentUserId = getCurrentUserId();
        log.info("Deleting multiple notifications for user: {}, IDs: {}", currentUserId, ids);
        inAppNotificationService.deleteMultipleNotifications(currentUserId, ids);
        log.info("Deleted notifications: {}", ids);
        return ResponseEntity.noContent().build();
    }

}
