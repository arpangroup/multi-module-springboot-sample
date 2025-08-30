package com.trustai.common.event;

import com.trustai.common.enums.TriggerType;
import lombok.Getter;

@Getter
public class UserRankUpdateEvent {
    private final Long userId;
    private final TriggerType triggerType;

    public UserRankUpdateEvent(Long userId, TriggerType triggerType) {
        this.userId = userId;
        this.triggerType = triggerType;
    }
}
