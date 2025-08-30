package com.trustai.common.event;

import lombok.Getter;

@Getter
public class UserActivatedActivityEvent extends UserActivityEvent {

    public UserActivatedActivityEvent(Object source, Long userId) {
        super(source, userId, "ACTIVATED");
    }
}
