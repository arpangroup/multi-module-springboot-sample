package com.trustai.notification_service.template.entity.data;

import com.trustai.notification_service.notification.entity.NotificationTemplate;
import com.trustai.notification_service.notification.enums.NotificationCode;
import com.trustai.notification_service.template.repository.TemplateRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationTemplateDataInitializer {
    private final TemplateRepository templateRepository;

    @PostConstruct
    public void init() {
        System.out.println("Starting initialization of notification templates...");
        var emailTemplates = getEmailTemplates();
        var smsTemplates = getSMSTemplates();
        var pushTemplates = getPushTemplates();

        System.out.println("Saving notification templates...");
        templateRepository.saveAll(emailTemplates);
        System.out.println("Saving smsTemplates...");
        templateRepository.saveAll(smsTemplates);
        System.out.println("Saving pushTemplates...");
        templateRepository.saveAll(pushTemplates);
    }

    private List<NotificationTemplate> getEmailTemplates() {
        return List.of(
                NotificationTemplate.email(
                        NotificationCode.MAIL_CONNECTION_TEST,
                        "SMTP Connection Test Successful",
                        "<b>Dear User,</b><br><br>"
                                + "This is to inform you that the SMTP connection test to your mail server has been successfully completed.<br><br>"
                                + "<b>Details:</b><br><br>"
                                + "- <b>SMTP Host:</b> smtp.gmail.com<br>"
                                + "- <b>SMTP Port:</b> 587<br>"
                                + "- <b>Connection Status:</b> Successful<br>"
                                + "- <b>Test Time:</b> [Insert Timestamp]<br><br>"
                                + "If you did not initiate this test or if you experience any issues with your email service, please contact your system administrator.<br><br>"
                                + "Thank you for using our service.<br><br>"
                                + "Best regards,<br>"
                                + "<b>Trust AI Support Team</b>"
                ),

        NotificationTemplate.email(NotificationCode.WELCOME_EMAIL,
                        "Welcome, {{name}}",
                        "Hi {{name}},\\n\\nThank you for signing up. Your verification code is {{code}}.\\n\\nRegards,\\nTeam"
                )
        );
    }

    private List<NotificationTemplate> getSMSTemplates() {
        return List.of(
                NotificationTemplate.sms(NotificationCode.OTP_SMS,
                        "Hello {{name}}, your OTP is {{code}}. It will expire in 5 minutes."
                )
        );
    }

    private List<NotificationTemplate> getPushTemplates() {
        return List.of(
                NotificationTemplate.push(NotificationCode.NEW_OFFER_PUSH,
                        "Exciting Offer for {{name}}!",
                        "Hi {{name}}, check out our latest offer. Use code {{code}} to save big!"
                )
        );
    }

}
