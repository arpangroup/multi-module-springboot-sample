package com.trustai.notification_service.template.entity.data;

import com.trustai.notification_service.template.entity.EmailTemplate;
import com.trustai.notification_service.notification.enums.NotificationCode;
import com.trustai.notification_service.template.repository.EmailTemplateRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmailTemplateDataInitializer {
    private final EmailTemplateRepository emailTemplateRepository;
    private final String TEMPLATE_FOR_ADMIN = "Admin";
    private final String TEMPLATE_FOR_USER = "User";
    private final String TEMPLATE_FOR_SUBSCRIBER = "Subscriber";

    @PostConstruct
    public void init() {
        emailTemplateRepository.saveAll(getTemplates());
    }

    private List<EmailTemplate> getTemplates() {
        return List.of(
                new EmailTemplate(NotificationCode.USER_MAIL_SEND, "{{subject}} for {{full_name}}")
                        .setBanner(null)
                        .setTitle("Sample Email")
                        .setSalutation("Hi {{full_name}},")
                        .setMessageBody("""
                        Thanks for joining us&nbsp;{{site_title}}
                        {{message}}
                        Find out more about in - {{site_url}}
                        """)
                        .setButtonLevel("Login Your Account")
                        .setButtonLink("https://trustai.co.in/login")
                        .setEnableFooterStatus(true)
                        .setFooterBody("""
                        Regards
                        {{site_title}}
                        """)
                        .setEnableFooterBottom(false)
                        .setBottomTitle("What is {{site_title}}")
                        .setBottomBody("""
                        TrustAI is a visual asset manager made for collaboration.
                        Build a central library for your team's visual assets.
                        Empower creation and ensure consistency from your desktop.
                        
                        {{site_url}}
                        """)
                        .setTemplateFor(TEMPLATE_FOR_USER)
                        .setTemplateActive(false),

                new EmailTemplate(NotificationCode.SUBSCRIBER_MAIL_SEND, "{{subject}} for {{full_name}}")
                        .setBanner(null)
                        .setTitle("Welcome to {{site_title}}")
                        .setSalutation("Hi {{full_name}},")
                        .setMessageBody("""
                        Thanks for joining our platform! ---  {{site_title}}
                        {{message}}
                        As a member of our platform, you can mange your account, buy or sell cryptocurrency, invest and earn profits.
                        Find out more about in - {{site_url}}
                        """)
                        .setButtonLevel("Login Your Account")
                        .setButtonLink("https://trustai.co.in/login")
                        .setEnableFooterStatus(true)
                        .setFooterBody("""
                        Thanks for joining our platform! ---  {{site_title}}
                        {{message}}
                        As a member of our platform, you can mange your account, buy or sell cryptocurrency, invest and earn profits.
                        Find out more about in - {{site_url}}
                        """)
                        .setEnableFooterBottom(true)
                        .setBottomTitle("What is {{site_title}}")
                        .setBottomBody("""
                        Hyiprio is a visual asset manager made for collaboration.
                        Build a central library for your team's visual assets.
                        Empower creation and ensure consistency from your desktop.
                        
                        {{site_url}}
                        """)
                        .setTemplateFor(TEMPLATE_FOR_SUBSCRIBER)
                        .setTemplateActive(false),
                new EmailTemplate(NotificationCode.EMAIL_VERIFICATION, "Verify Email Address")
                        .setBanner(null)
                        .setTitle("Verify Email Address")
                        .setSalutation("Hi {{full_name}},")
                        .setMessageBody("""
                        Hello!
                        
                        Please click the button below to verify your email address.
                        """)
                        .setButtonLevel("Verify Email Address")
                        .setButtonLink("{{accessToken}}")
                        .setEnableFooterStatus(true)
                        .setFooterBody("""
                        Regards,
                        {{site_title}}
                        """)
                        .setEnableFooterBottom(true)
                        .setBottomTitle("Verify Email Address")
                        .setBottomBody("""
                       If you're having trouble clicking the "Verify Email Address" button, copy\s
                       and paste the URL below into your web browser:{{accessToken}}
                       """)
                        .setTemplateFor(TEMPLATE_FOR_USER)
                        .setTemplateActive(true),
                new EmailTemplate(NotificationCode.FORGOT_PASSWORD, "User Forget Password")
                        .setBanner(null)
                        .setTitle("User Password Change")
                        .setSalutation("Hi user,")
                        .setMessageBody("""
                        Please click the button below to Change the Password.
                        """)
                        .setButtonLevel("Reset Password")
                        .setButtonLink("{{accessToken}}")
                        .setEnableFooterStatus(true)
                        .setFooterBody("""
                        Regards,
                        {{site_title}}
                        """)
                        .setEnableFooterBottom(true)
                        .setBottomTitle("User Password Change")
                        .setBottomBody("{{accessToken}}")
                        .setTemplateFor(TEMPLATE_FOR_USER)
                        .setTemplateActive(true),
                new EmailTemplate(NotificationCode.USER_INVESTMENT, "Thanks for the Investment on {{site_title}}")
                        .setBanner(null)
                        .setTitle("You Invested on {{plan_name}}")
                        .setSalutation("Hi {{full_name}},")
                        .setMessageBody("""
                         Hello!
                         {{txn}}. 'Successfully Investment
                         {{plan_name}}
                         {{invest_amount}}
                        """)
                        .setButtonLevel("Login Account")
                        .setButtonLink("{{accessToken}}")
                        .setEnableFooterStatus(true)
                        .setFooterBody("""
                        Regards,
                        {{site_title}}
                        """)
                        .setEnableFooterBottom(true)
                        .setBottomTitle("Successfully Investment")
                        .setBottomBody("{{txn}}. ' Successfully Investment")
                        .setTemplateFor(TEMPLATE_FOR_USER)
                        .setTemplateActive(true),
                new EmailTemplate(NotificationCode.USER_ACCOUNT_DISABLED, "Your Account is Disabled")
                        .setBanner(null)
                        .setTitle("Sorry to say that")
                        .setSalutation("Hi {{full_name}},")
                        .setMessageBody("""
                        Your account is disabled for a serious cause. Sorry! you're unable to use your account again.
                        """)
                        .setButtonLevel("Contact Us")
                        .setButtonLink("https://trustai.co.in/login")
                        .setEnableFooterStatus(true)
                        .setFooterBody("""
                        Regards,
                        {{site_title}}
                        """)
                        .setEnableFooterBottom(true)
                        .setBottomTitle("User Account Disabled")
                        .setBottomBody("{{full_name}}")
                        .setTemplateFor(TEMPLATE_FOR_USER)
                        .setTemplateActive(true),
                new EmailTemplate(NotificationCode.MANUAL_DEPOSIT_REQUEST, "Got a request for Manual Deposit")
                        .setBanner(null)
                        .setTitle("Manual Deposit request")
                        .setSalutation("Hi Admin,")
                        .setMessageBody("""
                        The manual deposit request details:
                        {{txn}}
                        {{gateway_name}}
                        {{deposit_amount}}
                        """)
                        .setButtonLevel("Review The requests")
                        .setButtonLink("https://trustai.co.in/admin")
                        .setEnableFooterStatus(true)
                        .setFooterBody("""
                        Regards,
                        {{site_title}}
                        """)
                        .setEnableFooterBottom(true)
                        .setBottomTitle("Manual Deposit request")
                        .setBottomBody("{{site_title}}")
                        .setTemplateFor(TEMPLATE_FOR_ADMIN)
                        .setTemplateActive(true),
                new EmailTemplate(NotificationCode.WITHDRAW_REQUEST, "{{subject}} for {{full_name}}")
                        .setBanner(null)
                        .setTitle("Manual Deposit request")
                        .setSalutation("Hi Admin,")
                        .setMessageBody("""
                        The manual deposit request details:
                        {{txn}}
                        {{gateway_name}}
                        {{deposit_amount}}
                        """)
                        .setButtonLevel("Review The requests")
                        .setButtonLink("https://trustai.co.in/admin")
                        .setEnableFooterStatus(true)
                        .setFooterBody("""
                        Regards,
                        {{site_title}}
                        """)
                        .setEnableFooterBottom(true)
                        .setBottomTitle("Manual Deposit request")
                        .setBottomBody("{{site_title}}")
                        .setTemplateFor(TEMPLATE_FOR_ADMIN)
                        .setTemplateActive(true),
                new EmailTemplate(NotificationCode.ADMIN_FORGET_PASSWORD, "Admin Forget Password")
                        .setBanner(null)
                        .setTitle("Admin Forget Password")
                        .setSalutation("Hi Admin,")
                        .setMessageBody("""
                        Please click the button below to reset your password
                        """)
                        .setButtonLevel("Review The requestsPassword reset")
                        .setButtonLink("{{accessToken}}")
                        .setEnableFooterStatus(true)
                        .setFooterBody("""
                        Regards,
                        {{site_title}}
                        """)
                        .setEnableFooterBottom(true)
                        .setBottomTitle("Withdraw Request")
                        .setBottomBody("""
                       {{full_name}}
                       {{accessToken}}
                       """)
                        .setTemplateFor(TEMPLATE_FOR_ADMIN)
                        .setTemplateActive(true),
                new EmailTemplate(NotificationCode.CONTACT_MAIL_SEND, "{{subject}} for {{full_name}}")
                        .setBanner(null)
                        .setTitle("Welcome to {{site_title}}")
                        .setSalutation("Hi {{full_name}},")
                        .setMessageBody("""
                        Thanks for joining our platform! ---  {{site_title}}
                        
                        {{message}}
                        {{full_name}}
                        {{email}}
                        
                        As a member of our platform, you can mange your account, buy or sell cryptocurrency, invest and earn profits.
                        
                        Find out more about in - {{site_url}}
                        """)
                        .setButtonLevel("Login Your Account")
                        .setButtonLink("https://trustai.co.in/login")
                        .setEnableFooterStatus(true)
                        .setFooterBody("""
                        Thanks for joining our platform! ---  {{site_title}}
                        
                        {{message}}
                        
                        As a member of our platform, you can mange your account, buy or sell cryptocurrency, invest and earn profits.
                        Find out more about in - {{site_url}}
                        """)
                        .setEnableFooterBottom(true)
                        .setBottomTitle("What is {{site_title}}")
                        .setBottomBody("""
                       Hyiprio is a visual asset manager made for collaboration. Build a central library for your team's visual assets. Empower creation and ensure consistency from your desktop.
                       {{site_url}}
                       """)
                        .setTemplateFor(TEMPLATE_FOR_ADMIN)
                        .setTemplateActive(true),
                new EmailTemplate(NotificationCode.KYC_REQUEST, "{{subject}} for {{full_name}}")
                        .setBanner(null)
                        .setTitle("Welcome to {{site_title}}")
                        .setSalutation("Hi {{full_name}},")
                        .setMessageBody("""
                        Thanks for joining our platform! ---  {{site_title}}
                        {{message}}
                        {{full_name}}
                        {{email}}
                        
                        As a member of our platform, you can mange your account, buy or sell cryptocurrency, invest and earn profits.
                        
                        
                        Find out more about in - {{site_url}}
                        """)
                        .setButtonLevel("Login Your Account")
                        .setButtonLink("https://trustai.co.in/login")
                        .setEnableFooterStatus(true)
                        .setFooterBody("""
                        Thanks for joining our platform! ---  {{site_title}}
                        
                        {{message}}
                        As a member of our platform, you can mange your account, buy or sell cryptocurrency, invest and earn profits.
                        
                        Find out more about in - {{site_url}}
                        """)
                        .setEnableFooterBottom(true)
                        .setBottomTitle("What is {{site_title}}")
                        .setBottomBody("""
                       Hyiprio is a visual asset manager made for collaboration. Build a central library for your team's visual assets. Empower creation and ensure consistency from your desktop.
                       {{site_url}}
                       """)
                        .setTemplateFor(TEMPLATE_FOR_ADMIN)
                        .setTemplateActive(true),
                new EmailTemplate(NotificationCode.KYC_ACTION, "{{subject}} for {{full_name}}")
                        .setBanner(null)
                        .setTitle("Welcome to {{site_title}}")
                        .setSalutation("Hi {{full_name}},")
                        .setMessageBody("""
                        Thanks for joining our platform! ---  {{site_title}}
                        
                        {{message}}
                        {{full_name}}
                        {{email}}
                        
                        As a member of our platform, you can mange your account, buy or sell cryptocurrency, invest and earn profits.
                        
                        Find out more about in - {{site_url}}
                        """)
                        .setButtonLevel("Login Your Account")
                        .setButtonLink("https://trustai.co.in/login")
                        .setEnableFooterStatus(true)
                        .setFooterBody("""
                        Thanks for joining our platform! ---  {{site_title}}
                        
                        {{message}}
                        As a member of our platform, you can mange your account, buy or sell cryptocurrency, invest and earn profits.
                        Find out more about in - {{site_url}}
                        """)
                        .setEnableFooterBottom(true)
                        .setBottomTitle("What is {{site_title}}")
                        .setBottomBody("""
                       Hyiprio is a visual asset manager made for collaboration. Build a central library for your team's visual assets. Empower creation and ensure consistency from your desktop.
                       {{site_url}}
                       """)
                        .setTemplateFor(TEMPLATE_FOR_ADMIN)
                        .setTemplateActive(true),
                new EmailTemplate(NotificationCode.INVEST_ROI, "Thanks for the Investment on {{site_title}}")
                .setBanner(null)
                .setTitle("You Invested on {{plan_name}}")
                .setSalutation("Hi {{full_name}},")
                .setMessageBody("""
                       Hello!
                       
                       {{txn}}. 'Successfully Investment
                       {{plan_name}}
                       {{invest_amount}}
                       {{roi}}
                       """)
                .setButtonLevel("Login Your Account")
                .setButtonLink("https://trustai.co.in/login")
                .setEnableFooterStatus(true)
                .setFooterBody("""                            
                     Regards
                     {{site_title}}
                     """)
                .setEnableFooterBottom(false)
                .setBottomTitle("Successfully ROI")
                .setBottomBody("{{txn}}. ' Successfully ROI")
                .setTemplateFor(TEMPLATE_FOR_USER)
                .setTemplateActive(true),
                new EmailTemplate(NotificationCode.INVESTMENT_END, "Thanks for the Investment on {{site_title}}")
                        .setBanner(null)
                        .setTitle("You Invested on {{plan_name}}")
                        .setSalutation("Hi {{full_name}},")
                        .setMessageBody("""
                           Hello!
                           {{txn}}. 'Successfully Investment
                           {{plan_name}}
                           {{invest_amount}}
                        """)
                        .setButtonLevel("Login Your Account")
                        .setButtonLink("https://trustai.co.in/login")
                        .setEnableFooterStatus(true)
                        .setFooterBody("""                            
                        Regards
                        {{site_title}}
                        """)
                        .setEnableFooterBottom(false)
                        .setBottomTitle("Successfully Investment")
                        .setBottomBody("{{txn}}. ' Successfully ROISuccessfully Investment End")
                        .setTemplateFor(TEMPLATE_FOR_USER)
                        .setTemplateActive(true),
                new EmailTemplate(NotificationCode.WITHDRAW_REQUEST_ACTION, "Got a Withdraw Request")
                        .setBanner(null)
                        .setTitle("Withdraw Request")
                        .setSalutation("Hi {{full_name}},")
                        .setMessageBody("""
                        Withdraw Request details:
                        {{message}}
                        {{txn}}
                        {{method_name}}
                        {{withdraw_amount}}
                        {{status}}
                        """)
                        .setButtonLevel("Withdraw Request")
                        .setButtonLink("{{site_url}}")
                        .setEnableFooterStatus(true)
                        .setFooterBody("""                            
                        Regards
                        {{site_title}}
                        """)
                        .setEnableFooterBottom(false)
                        .setBottomTitle("Withdraw Request")
                        .setBottomBody("{{full_name}}")
                        .setTemplateFor(TEMPLATE_FOR_USER)
                        .setTemplateActive(true),
                new EmailTemplate(NotificationCode.MANUAL_DEPOSIT_REQUEST_ACTION, "Got a request for Manual Deposit")
                        .setBanner(null)
                        .setTitle("Manual Deposit request")
                        .setSalutation("Hi {{full_name}},")
                        .setMessageBody("""
                        The manual deposit request details:
                        {{message}}
                        {{txn}}
                        {{gateway_name}}
                        {{deposit_amount}}
                        {{status}}
                        """)
                        .setButtonLevel("Review The requests")
                        .setButtonLink("{{site_url}}")
                        .setEnableFooterStatus(true)
                        .setFooterBody("""                            
                        Regards
                        {{site_title}}
                        """)
                        .setEnableFooterBottom(false)
                        .setBottomTitle("Manual Deposit request")
                        .setBottomBody("{{full_name}}")
                        .setTemplateFor(TEMPLATE_FOR_USER)
                        .setTemplateActive(true),
                new EmailTemplate(NotificationCode.USER_SUPPORT_TICKET, "Support Ticket")
                        .setBanner(null)
                        .setTitle("Support Ticket")
                        .setSalutation("Hi {{full_name}},")
                        .setMessageBody("""
                        Support Ticket:
                        {{title}}
                        {{message}}
                        {{status}}
                        """)
                        .setButtonLevel("...")
                        .setButtonLink("{{site_url}}")
                        .setEnableFooterStatus(true)
                        .setFooterBody("""                            
                        Regards
                        {{site_title}}
                        """)
                        .setEnableFooterBottom(false)
                        .setBottomTitle("Support Ticket")
                        .setBottomBody("{{full_name}}")
                        .setTemplateFor(TEMPLATE_FOR_USER)
                        .setTemplateActive(true),
                new EmailTemplate(NotificationCode.ADMIN_SUPPORT_TICKET, "Support Ticket")
                        .setBanner(null)
                        .setTitle("Support Ticket")
                        .setSalutation("Hi {{full_name}},")
                        .setMessageBody("""
                        Support Ticket:
                        {{title}}
                        {{message}}
                        {{status}}
                        """)
                        .setButtonLevel("...")
                        .setButtonLink("{{site_url}}")
                        .setEnableFooterStatus(true)
                        .setFooterBody("""                            
                        Regards
                        {{site_title}}
                        """)
                        .setEnableFooterBottom(false)
                        .setBottomTitle("Support Ticket")
                        .setBottomBody("{{full_name}}")
                        .setTemplateFor(TEMPLATE_FOR_ADMIN)
                        .setTemplateActive(true)
        );
    }

}
