package com.trustai.common.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/*
combining DepositEvent, ReferralJoinedEvent, and UserActivatedEvent
 */
@Getter
public abstract class UserActivityEvent extends ApplicationEvent {
    private final Long userId;
    private final LocalDateTime activityTime;
    private final String activityType; // e.g. DEPOSIT, ACTIVATION, REFERRAL_JOINED

    public UserActivityEvent(Object source, Long userId, String activityType) {
        super(source);
        this.userId = userId;
        this.activityType = activityType;
        this.activityTime = LocalDateTime.now();
    }
}
