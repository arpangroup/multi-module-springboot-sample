package com.trustai.notification_service.notification.service;

import com.trustai.notification_service.notification.dto.InAppNotificationDto;
import com.trustai.notification_service.notification.mapper.InAppNotificationMapper;
import com.trustai.notification_service.notification.repository.InAppNotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InAppNotificationService {
    private final InAppNotificationRepository repository;
    private final InAppNotificationMapper mapper;

    public Page<InAppNotificationDto> getUserNotifications(Long userId, int page, int size, String sortBy, String sortDir) {
        log.info("Fetching notifications for user: {}, page: {}, size: {}, sortBy: {}, sortDir: {}",
                userId, page, size, sortBy, sortDir);
        Pageable pageable = PageRequest.of(
                page, size,
                sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending()
        );

        Page<InAppNotificationDto> result = repository.findByUserIdAndDeletedFalse(userId, pageable).map(mapper::toDto);
        log.info("Fetched {} notifications for user: {}", result.getNumberOfElements(), userId);

        return result;
    }

    @Transactional
    public void markAsViewed(Long userId, Long id) {
        log.info("Marking notification {} as viewed for user: {}", id, userId);
        repository.updateViewedStatus(userId, id, true);
        log.info("Notification {} marked as viewed for user: {}", id, userId);
    }

    @Transactional
    public void markMultipleAsViewed(Long userId, List<Long> ids) {
        log.info("Marking multiple notifications as viewed for user: {}, IDs: {}", userId, ids);
        repository.updateViewedStatusForIds(userId, ids, true);
        log.info("Marked {} notifications as viewed for user: {}", ids.size(), userId);
    }

    @Transactional
    public void deleteNotification(Long userId, Long id) {
        log.info("Deleting notification {} for user: {}", id, userId);
        repository.softDeleteByUserIdAndId(userId, id);
        log.info("Notification {} deleted for user: {}", id, userId);
    }

    @Transactional
    public void deleteMultipleNotifications(Long userId, List<Long> ids) {
        log.info("Deleting multiple notifications for user: {}, IDs: {}", userId, ids);
        repository.softDeleteByUserIdAndIdIn(userId, ids);
        log.info("Deleted {} notifications for user: {}", ids.size(), userId);
    }
}
