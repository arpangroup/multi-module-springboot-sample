package com.trustai.notification_service.notification.service;

public interface EmailService {
    // send a simple email
    void sendSimpleMail(String to, String subject, String text);
    void sendMail(String to, String subject, String htmlContent);

    // send an email with attachment
    void sendMailWithAttachment(String to, String subject, String text, String pathToAttachment);
}
