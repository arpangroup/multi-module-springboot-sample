package com.trustai.notification_service.notification.sender;

import com.trustai.common.enums.NotificationChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class NotificationSenderFactory {
    private final Map<NotificationChannel, NotificationSender> senderMap;

    public NotificationSenderFactory(List<NotificationSender> senders) {
        this.senderMap = senders.stream()
                .collect(Collectors.toMap(NotificationSender::getChannel, Function.identity()));
        log.info("NotificationSenderFactory initialized with channels: {}", senderMap.keySet());
    }

    public NotificationSender getSender(NotificationChannel channel) {
        NotificationSender sender = senderMap.get(channel);
        if (sender != null) {
            log.info("Found sender for channel: {}", channel);
        } else {
            log.warn("No sender found for channel: {}", channel);
        }
        return sender;
    }
}
