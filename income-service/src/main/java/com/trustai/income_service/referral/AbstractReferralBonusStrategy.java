package com.trustai.income_service.referral;

import com.trustai.common.api.TransactionApi;
import com.trustai.common.api.UserApi;
import com.trustai.common.api.WalletApi;
import com.trustai.common.dto.UserInfo;
import com.trustai.common.dto.WalletUpdateRequest;
import com.trustai.common.enums.CalculationType;
import com.trustai.common.enums.TransactionType;
import com.trustai.common.enums.TriggerType;
import com.trustai.income_service.constant.Remarks;
import com.trustai.income_service.referral.calculator.BonusAmountCalculator;
import com.trustai.income_service.referral.calculator.BonusAmountCalculatorFactory;
import com.trustai.income_service.referral.entity.BonusStatus;
import com.trustai.income_service.referral.entity.ReferralBonus;
import com.trustai.income_service.referral.repository.ReferralBonusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

@Slf4j
public abstract class AbstractReferralBonusStrategy implements ReferralBonusStrategy {
    @Autowired protected ReferralBonusRepository bonusRepository;
    @Autowired protected UserApi userClient;
    @Autowired protected WalletApi walletClient;
    @Autowired protected TransactionApi transactionClient;
    protected BonusAmountCalculator calculator;

    @Autowired
    public void setCalculator(
            BonusAmountCalculatorFactory calculatorFactory,
            @Value("${bonus.referral.calculation-type}") CalculationType calculationType
    ) {
        this.calculator = calculatorFactory.getCalculator(calculationType);
    }

    @Override
    public void applyBonus(UserInfo referrer, UserInfo referee) {
        log.info("Apply Bonus for referrer: {}, referee: {}......", referrer.getId(), referrer.getId());
        BigDecimal bonusAmount = getBonusAmount(referrer, referee);
        depositReferralBonus(referrer.getId(), bonusAmount);

        ReferralBonus bonus = bonusRepository.findByReferrerIdAndRefereeIdAndStatus(referrer.getId(), referee.getId(), BonusStatus.PENDING).orElse(null);
        if (bonus == null) { // DIRECT APPROVE
            bonus = new ReferralBonus();
            bonus.setReferrerId(referrer.getId());
            bonus.setRefereeId(referee.getId());
            bonus.setBonusAmount(bonusAmount);
            bonus.setTriggerType(getTriggerType());
        }
        bonus.setStatus(BonusStatus.APPROVED);
        bonus.setRemarks(Remarks.REFERRAL_BONUS);
        bonusRepository.save(bonus);
    }

    @Override
    public boolean processPendingBonus(ReferralBonus bonus) {
        Long refereeId = bonus.getRefereeId();
        UserInfo referee = userClient.getUserById(refereeId);
        //UserInfo referrer = userClient.getUserInfo(bonus.getReferrerId());

        if (!isEligible(referee)) {
            return false;
        }

        depositReferralBonus(bonus.getReferrerId(), bonus.getBonusAmount());
        return true;
    }

    private void depositReferralBonus(long userId, BigDecimal bonusAmount) {
        //userClient.deposit(userId, bonusAmount, Remarks.REFERRAL_BONUS, null);
        WalletUpdateRequest depositReferralBonusRequest = new WalletUpdateRequest(
                bonusAmount,
                TransactionType.REFERRAL,
                true,
                "referral-income",
                Remarks.REFERRAL_BONUS,
                null
        );
        walletClient.updateWalletBalance(userId, depositReferralBonusRequest);
    }

    // Allow strategy to provide custom amount and trigger type
    protected abstract BigDecimal getBonusAmount(UserInfo referrer, UserInfo referee);
    protected abstract TriggerType getTriggerType();
}
