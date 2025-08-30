package com.trustai.common.dto;

import com.trustai.common.enums.NotificationChannel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@ToString
public class NotificationRequest {

    // Targeting (Required fields)
    private String recipient;                  // Email, phone number, or user ID (null if sendToAll is true)
    private boolean sendToAll;                 // Send to all users (e.g. announcements)

    // Content (raw or templated)
    private String subject;                    // For email or push
    private String title;                      // For in-app notifications
    private String message;                    // Body text, optional if using template
    private String templateCode;               // Optional: Code to lookup a predefined template
    private Map<String, String> properties;    // For template variable injection

    // Channel & delivery
    private List<NotificationChannel> channels; // Channels to send through (EMAIL, SMS, PUSH, IN_APP, etc.)
    private boolean immediate = true;           // true = send now, false = schedule later

    // Extra
    private Map<String, Object> metadata;       // For templating, tracking, logging, etc.

    public static NotificationRequest forEmail(String recipient, String subject, String message) {
        NotificationRequest req = new NotificationRequest();
        req.setRecipient(recipient);
        req.setSubject(subject);
        req.setMessage(message);
        //req.setChannels(List.of(NotificationChannel.EMAIL)); // ⚠️ Don't use immutable list – can't add more channels later
        req.setChannels(new ArrayList<>(List.of(NotificationChannel.EMAIL))); // ✅ mutable list
        return req;
    }

    public static NotificationRequest forSMS(String recipient, String message) {
        NotificationRequest req = new NotificationRequest();
        req.setRecipient(recipient);
        req.setMessage(message);
        //req.setChannels(List.of(NotificationChannel.SMS)); // ⚠️ Don't use immutable list – can't add more channels later
        req.setChannels(new ArrayList<>(List.of(NotificationChannel.SMS))); // ✅ mutable list
        return req;
    }

    public static NotificationRequest forInApp(String userId, String title, String message) {
        NotificationRequest req = new NotificationRequest();
        req.setRecipient(userId);
        req.setTitle(title);
        req.setMessage(message);
        //req.setChannels(List.of(NotificationChannel.IN_APP)); // ⚠️ Don't use immutable list – can't add more channels later
        req.setChannels(new ArrayList<>(List.of(NotificationChannel.IN_APP))); // ✅ mutable list
        return req;
    }

    public static NotificationRequest broadcastInApp(String title, String message) {
        NotificationRequest req = new NotificationRequest();
        req.setSendToAll(true);
        req.setTitle(title);
        req.setMessage(message);
        //req.setChannels(List.of(NotificationChannel.IN_APP)); // ⚠️ Don't use immutable list – can't add more channels later
        req.setChannels(new ArrayList<>(List.of(NotificationChannel.IN_APP))); // ✅ mutable list
        return req;
    }

    public static NotificationRequest forTemplate(String recipient, String templateCode, Map<String, String> props, List<NotificationChannel> channels) {
        NotificationRequest req = new NotificationRequest();
        req.setRecipient(recipient);
        req.setTemplateCode(templateCode);
        req.setProperties(props);
        req.setChannels(channels);
        return req;
    }

    public List<NotificationChannel> getChannels() {
        if (this.channels == null) {
            this.channels = new ArrayList<>();
        }
        return this.channels;
    }
}
