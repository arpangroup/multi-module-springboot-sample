package com.trustai.common.enums;

public enum TriggerType {
    REGISTRATION("REGISTRATION"),                   // Bonus triggered on downline user's registration
    FIRST_DEPOSIT("FIRST_DEPOSIT"),                 // Bonus triggered on user's first deposit
    ACCOUNT_ACTIVATION("ACCOUNT_ACTIVATION"),       // Bonus triggered when account is activated (e.g., payment or KYC)
    MINIMUM_REQUIREMENT("MINIMUM_REQUIREMENT"),     // Bonus triggered after meeting a custom condition (e.g., invite 3 users)
    MANUAL_APPROVAL("MANUAL_APPROVAL");             // Bonus triggered only via manual admin approval


    private final String label;

    TriggerType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static TriggerType fromLabel(String label) {
        for (TriggerType type : TriggerType.values()) {
            if (type.getLabel().equalsIgnoreCase(label)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No matching ReferralBonusTriggerType for label: " + label);
    }
}
