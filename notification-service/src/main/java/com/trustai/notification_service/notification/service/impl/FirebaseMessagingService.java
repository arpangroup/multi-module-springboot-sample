/*
package com.trustai.notification_service.notification.service.impl;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FirebaseMessagingService {
    private final FirebaseMessaging firebaseMessaging; // From Firebase Admin SDK

    public void sendNotification(String recipient, String title, String body) {
        // Prepare Firebase push message
        Message message = Message.builder()
                .setToken(recipient) // FCM accessToken
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        try {
            String response = firebaseMessaging.send(message);
            System.out.println("Push sent: " + response);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException("Failed to send push notification", e);
        }
    }
}
*/
