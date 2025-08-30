package com.trustai.common.event;

import com.trustai.common.dto.NotificationRequest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NotificationEvent  extends ApplicationEvent {
    private final NotificationRequest request;

    public NotificationEvent(Object source, NotificationRequest request) {
        super(source);
        this.request = request;
    }

}
