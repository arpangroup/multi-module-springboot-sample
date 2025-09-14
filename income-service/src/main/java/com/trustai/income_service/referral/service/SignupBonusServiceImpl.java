package com.trustai.income_service.referral.service;

import com.trustai.common.api.UserApi;
import com.trustai.common.api.WalletApi;
import com.trustai.common.dto.UserInfo;
import com.trustai.common.dto.WalletUpdateRequest;
import com.trustai.common.enums.IncomeType;
import com.trustai.common.enums.TransactionType;
import com.trustai.income_service.constant.Remarks;
import com.trustai.income_service.income.entity.IncomeHistory;
import com.trustai.income_service.income.repository.IncomeHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignupBonusServiceImpl implements SignupBonusService {
    private final IncomeHistoryRepository incomeRepo;
    private final UserApi userApi;
    private final WalletApi walletApi;

    @Value("${bonus.signup.flat-amount}")
    private BigDecimal signupBonus;

    @Override
    public void applySignupBonus(Long userId) {
        log.info("applySignupBonus for userId: {}.........", userId);
        UserInfo user = userApi.getUserById(userId);

        // 1. Save users Signup bonus income
        log.info("Saving signup bonus income of {} for user: {}...........", signupBonus, userId);
        IncomeHistory incomeHistory = IncomeHistory.builder()
                .userId(userId)
                .amount(signupBonus)
                .incomeType(IncomeType.SIGNUP_BONUS)
                .sourceUserId(userId)
                .sourceUserRank(null)
                .note(Remarks.WELCOME_BONUS)
                .build();
        incomeRepo.save(incomeHistory);
        log.info("Saved direct income of {} for user {}", signupBonus, userId);

        // 2. Update Wallet Balance
        log.info("Updating the wallet for UserID: {} with SignupBonus: {}", userId, signupBonus);
        WalletUpdateRequest depositRequest = new WalletUpdateRequest(
                signupBonus,
                TransactionType.SIGNUP_BONUS,
                true,
                "signup-bonus",
                Remarks.WELCOME_BONUS,
                null
        );
        walletApi.updateWalletBalance(userId, depositRequest);
    }
}
