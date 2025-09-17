package com.trustai.userservice.listener;

import com.trustai.common.domain.user.User;
import com.trustai.common.event.FirstDepositEvent;
import com.trustai.userservice.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class FirstDepositUserStatusListener {
    private final UserProfileService userProfileService;

    @EventListener
    public void handleFirstDepositEvent(FirstDepositEvent event) {
        Long userId = event.getUserId();
        BigDecimal amount = event.getAmount();

        log.info("📥 FirstDepositEvent received | userId={}, amount={}", userId, amount);

        try {
            log.info("🔄 Updating user status to ACTIVE | userId={}", userId);
            userProfileService.updateUserStatus(userId, User.AccountStatus.ACTIVE);
            log.info("✅ User status updated successfully | userId={}", userId);
        } catch (Exception e) {
            log.error("❌ Failed to update user status | userId={}, amount={}", userId, amount, e);
        }
    }
}