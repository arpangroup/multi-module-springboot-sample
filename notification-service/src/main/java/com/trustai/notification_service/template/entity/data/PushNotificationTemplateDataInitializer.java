package com.trustai.notification_service.template.entity.data;

import com.trustai.notification_service.notification.enums.NotificationCode;
import com.trustai.notification_service.template.entity.PushNotificationTemplate;
import com.trustai.notification_service.template.repository.PushNotificationTemplateRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PushNotificationTemplateDataInitializer {
    private final PushNotificationTemplateRepository pushTemplateRepository;
    private final String TEMPLATE_FOR_ADMIN = "Admin";
    private final String TEMPLATE_FOR_USER = "USER";

    @PostConstruct
    public void init() {
        List<PushNotificationTemplate> smsTemplates = getTemplates();
        pushTemplateRepository.saveAll(smsTemplates);
    }

    private List<PushNotificationTemplate> getTemplates() {
        return List.of(
            new PushNotificationTemplate(NotificationCode.NEW_USER)
                    .setTitle("Wellcome to {{full_name}}")
                    .setMessageBody("""
                            Thanks for joining us  {{full_name}}
                            
                            {{message}}
                            """)
                    .setTemplateFor(TEMPLATE_FOR_ADMIN)
                    .setTemplateActive(true),

            new PushNotificationTemplate(NotificationCode.MANUAL_DEPOSIT_REQUEST)
                    .setTitle("Manual Deposit request")
                    .setMessageBody("""
                           The manual deposit request details:
                           {{txn}}
                           {{gateway_name}}
                           {{deposit_amount}}
                           """)
                    .setTemplateFor(TEMPLATE_FOR_ADMIN)
                    .setTemplateActive(true),
            new PushNotificationTemplate(NotificationCode.WITHDRAW_REQUEST)
                    .setTitle("Withdraw Request")
                    .setMessageBody("""
                           Withdraw Request details:
                           {{txn}}
                           {{method_name}}
                           {{withdraw_amount}}
                           """)
                    .setTemplateFor(TEMPLATE_FOR_ADMIN)
                    .setTemplateActive(true),
            new PushNotificationTemplate(NotificationCode.KYC_REQUEST)
                    .setTitle("Kyc Request")
                    .setMessageBody("""
                            {{full_name}} Kyc requested
                            {{email}}
                            """)
                    .setTemplateFor(TEMPLATE_FOR_ADMIN)
                    .setTemplateActive(true),
            new PushNotificationTemplate(NotificationCode.KYC_ACTION),
            new PushNotificationTemplate(NotificationCode.USER_INVESTMENT_START)
                    .setTitle("You Invested on {{plan_name}}")
                    .setMessageBody("""
                           Hello!
                           {{txn}} 'Successfully Investment
                           {{plan_name}}
                           {{invest_amount}}
                           """)
                    .setTemplateFor(TEMPLATE_FOR_ADMIN)
                    .setTemplateActive(true),
            new PushNotificationTemplate(NotificationCode.INVESTED_ON_PROFIT)
                    .setTitle("You Invested on {{plan_name}}")
                    .setMessageBody("""
                           Hello!
                           {{txn}} 'Successfully Investment
                           {{plan_name}
                           {{invest_amount}}
                           {{roi}}
                           """)
                    .setTemplateFor(TEMPLATE_FOR_ADMIN)
                    .setTemplateActive(true),
            new PushNotificationTemplate(NotificationCode.INVESTMENT_END)
                    .setTitle("You Invested on {{plan_name}}")
                    .setMessageBody("""
                           Hello!
                           {{txn}} 'Successfully Investment End
                           {{plan_name}}
                           {{invest_amount}}
                           """)
                    .setTemplateFor(TEMPLATE_FOR_ADMIN)
                    .setTemplateActive(true),
            new PushNotificationTemplate(NotificationCode.WITHDRAW_REQUEST_ACTION)
                    .setTitle("Withdraw Request")
                    .setMessageBody("""
                           Withdraw Request details:
                           {{message}}
                           {{txn}}
                           {{method_name}}
                           {{withdraw_amount}}
                           {{status}}
                           """),
            new PushNotificationTemplate(NotificationCode.MANUAL_DEPOSIT_REQUEST_ACTION)
                    .setTitle("Manual Deposit request")
                    .setMessageBody("""
                           The manual deposit request details:
                           {{message}}
                           {{txn}}
                           {{gateway_name}}
                           {{deposit_amount}}
                           {{status}}
                           """)
        );
    }

}
