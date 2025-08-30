package com.trustai.notification_service.notification.repository;

import com.trustai.notification_service.notification.entity.InAppNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InAppNotificationRepository extends JpaRepository<InAppNotification, Long> {

    //Page<InAppNotification> findByUserId(Long userId, Pageable pageable);
    Page<InAppNotification> findByUserIdAndDeletedFalse(Long userId, Pageable pageable);

    @Modifying
    @Query("UPDATE InAppNotification n SET n.viewed = :viewed WHERE n.userId = :userId AND n.id = :id")
    void updateViewedStatus(Long userId, Long id, boolean viewed);

    @Modifying
    @Query("UPDATE InAppNotification n SET n.viewed = :viewed WHERE n.userId = :userId AND n.id IN :ids")
    void updateViewedStatusForIds(Long userId, List<Long> ids, boolean viewed);

    @Modifying
    @Query("UPDATE InAppNotification n SET n.deleted = true WHERE n.userId = :userId AND n.id = :id")
    void softDeleteByUserIdAndId(Long userId, Long id);

    //void deleteByUserIdAndId(Long userId, Long id);

    //void deleteByUserIdAndIdIn(Long userId, List<Long> ids);

    @Modifying
    @Query("UPDATE InAppNotification n SET n.deleted = true WHERE n.userId = :userId AND n.id IN :ids")
    void softDeleteByUserIdAndIdIn(Long userId, List<Long> ids);
}
