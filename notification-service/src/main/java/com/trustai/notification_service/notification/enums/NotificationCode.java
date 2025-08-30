package com.trustai.notification_service.notification.enums;

public enum NotificationCode {
    MAIL_CONNECTION_TEST,
    WELCOME_EMAIL,
    OTP_SMS,
    NEW_OFFER_PUSH,

    // EMAIL:
    USER_MAIL_SEND,
    SUBSCRIBER_MAIL_SEND,
    EMAIL_VERIFICATION,
    FORGOT_PASSWORD,
    USER_INVESTMENT,                // EMAIL, SMS
    USER_ACCOUNT_DISABLED,          // EMAIL, SMS
    MANUAL_DEPOSIT_REQUEST,         // EMAIL, SMS, PUSH
    WITHDRAW_REQUEST,               // EMAIL, SMS, PUSH
    ADMIN_FORGET_PASSWORD,
    CONTACT_MAIL_SEND,
    KYC_REQUEST,                    // SMS, PUSH
    KYC_ACTION,                     // EMAIL, SMS, PUSH
    INVEST_ROI,                     // EMAIL, SMS
    WITHDRAW_REQUEST_ACTION,        // EMAIL, SMS, PUSH
    MANUAL_DEPOSIT_REQUEST_ACTION,  // EMAIL, SMS, PUSH
    USER_SUPPORT_TICKET,
    ADMIN_SUPPORT_TICKET,

    // SMS:
    NEW_USER,                       // SMS, PUSH

    // PUSH
    USER_INVESTMENT_START,          // PUSH
    INVESTED_ON_PROFIT,             // PUSH
    INVESTMENT_END,                 // PUSH

}
