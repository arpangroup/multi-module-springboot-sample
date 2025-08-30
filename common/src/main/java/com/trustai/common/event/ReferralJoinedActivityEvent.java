package com.trustai.common.event;

import lombok.Getter;

@Getter
public class ReferralJoinedActivityEvent extends UserActivityEvent {
    private final Long newReferralUserId;

    public ReferralJoinedActivityEvent(Object source, Long referrerId, Long newReferralUserId) {
        super(source, referrerId, "REFERRAL_JOINED");
        this.newReferralUserId = newReferralUserId;
    }

    public Long getReferrerId() {
        return super.getUserId();
    }
}
