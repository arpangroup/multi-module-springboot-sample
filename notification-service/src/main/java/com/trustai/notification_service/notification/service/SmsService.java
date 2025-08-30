package com.trustai.notification_service.notification.service;

public interface SmsService {
    void sendSms(String recipient, String message);
}
