package com.trustai.userservice.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_activity_log")
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityLog {
    @Id
    @GeneratedValue
    private Long id;
    private Long userId;
    private String action;     // e.g., RANK_CHANGE, LOGIN, DEPOSIT
    private String details;    // description
    private LocalDateTime timestamp;

    public static UserActivityLog rankChanged(Long userId, String oldRank, String newRank, String source) {
        return new UserActivityLog(userId, "RANK_CHANGE",
                String.format("Rank changed from %s to %s due to %s", oldRank, newRank, source),
                LocalDateTime.now()
        );
    }

    public UserActivityLog(Long userId, String action, String details, LocalDateTime timestamp) {
        this.userId = userId;
        this.action = action;
        this.details = details;
        this.timestamp = timestamp;
    }
}
