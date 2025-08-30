package com.trustai.notification_service.template.entity.data;

import com.trustai.notification_service.notification.enums.NotificationCode;
import com.trustai.notification_service.template.entity.SmsTemplate;
import com.trustai.notification_service.template.repository.SmsTemplateRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SmsTemplateDataInitializer {
    private final SmsTemplateRepository smsTemplateRepository;
    private final String TEMPLATE_FOR_ADMIN = "Admin";
    private final String TEMPLATE_FOR_USER = "USER";

    @PostConstruct
    public void init() {
        smsTemplateRepository.saveAll(getTemplates());
    }

    private List<SmsTemplate> getTemplates() {
        return List.of(
            new SmsTemplate(NotificationCode.NEW_USER)
                    .setMessageBody("""
                            Thanks for joining us  {{full_name}}
                            {{message}}
                            """),

                new SmsTemplate(NotificationCode.USER_INVESTMENT)
                        .setMessageBody("""
                               Hello!
                               {{txn}}. 'Successfully Investment
                               {{plan_name}}
                               {{invest_amount}}
                            """),
                new SmsTemplate(NotificationCode.USER_ACCOUNT_DISABLED),
                new SmsTemplate(NotificationCode.MANUAL_DEPOSIT_REQUEST)
                        .setMessageBody("""
                                The manual deposit request details:
                                {{txn}}
                                {{gateway_name}}
                                {{deposit_amount}}
                                """),
                new SmsTemplate(NotificationCode.WITHDRAW_REQUEST)
                        .setMessageBody("""
                                Withdraw Request details:
                                {{txn}}
                                {{method_name}}
                                {{withdraw_amount}}
                                """),
                new SmsTemplate(NotificationCode.KYC_REQUEST)
                        .setMessageBody("""
                                Thanks for joining our platform! ---  {{site_title}}
                                
                                {{full_name}}
                                {{email}}
                                
                                As a member of our platform, you can mange your account, buy or sell cryptocurrency, invest and earn profits.
                                
                                Find out more about in - {{site_url}}
                                """),
                new SmsTemplate(NotificationCode.KYC_ACTION)
                        .setMessageBody("""
                                Thanks for joining our platform! ---  {{site_title}}
                                                    
                                {{message}}
                                {{full_name}}
                                {{email}}
                                
                                As a member of our platform, you can mange your account, buy or sell cryptocurrency, invest and earn profits.
                                
                                Find out more about in - {{site_url}}
                                """),
                new SmsTemplate(NotificationCode.INVEST_ROI)
                        .setMessageBody("""
                                Hello!
                                {{txn}}. 'Successfully Investment
                                {{plan_name}}
                                {{invest_amount}}
                                {{roi}}
                                """),
                new SmsTemplate(NotificationCode.INVESTMENT_END)
                        .setMessageBody("""
                                Hello!
                                {{txn}}. 'Successfully Investment End
                                {{plan_name}}
                                {{invest_amount}}
                                """),
                new SmsTemplate(NotificationCode.WITHDRAW_REQUEST_ACTION)
                        .setMessageBody("""
                                Withdraw Request details:
                                {{message}}
                                {{txn}}
                                {{method_name}}
                                {{withdraw_amount}}
                                {{status}}
                                """),
                new SmsTemplate(NotificationCode.MANUAL_DEPOSIT_REQUEST_ACTION)
                        .setMessageBody("""
                                The manual deposit request details:
                                [[message]]
                                [[txn]]
                                [[gateway_name]]
                                [[deposit_amount]]
                                [[status]]
                                """)
        );
    }

}
