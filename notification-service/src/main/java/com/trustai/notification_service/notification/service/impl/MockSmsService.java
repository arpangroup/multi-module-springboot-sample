package com.trustai.notification_service.notification.service.impl;

import com.trustai.notification_service.notification.service.SmsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class MockSmsService implements SmsService {
    @Override
    public void sendSms(String recipient, String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
        System.out.println("====== SMS LOG ======");
        System.out.println("Timestamp : " + timestamp);
        System.out.println("Recipient : " + recipient);
        System.out.println("Message   : " + message);
        System.out.println("=====================\n");
    }
}
