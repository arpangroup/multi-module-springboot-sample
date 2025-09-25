package com.trustai.income_service.referral;

import com.trustai.common.api.UserApi;
import com.trustai.common.api.WalletApi;
import com.trustai.common.dto.UserInfo;
import com.trustai.common.dto.WalletUpdateRequest;
import com.trustai.common.enums.CalculationType;
import com.trustai.common.enums.IncomeType;
import com.trustai.common.enums.TransactionType;
import com.trustai.common.enums.TriggerType;
import com.trustai.income_service.config.ReferralBonusConfigProperty;
import com.trustai.income_service.constant.Remarks;
import com.trustai.income_service.income.service.IncomeHistoryService;
import com.trustai.income_service.referral.entity.BonusStatus;
import com.trustai.income_service.referral.entity.ReferralBonus;
import com.trustai.income_service.referral.repository.ReferralBonusRepository;
import com.trustai.income_service.referral.service.ReferralBonusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReferralBonusServiceImpl implements ReferralBonusService {
    private final UserApi userApi;
    private final Map<String, ReferralBonusStrategy> strategies;
    private final ReferralBonusRepository bonusRepository;

    @Autowired
    IncomeHistoryService incomeHistoryService;

    @Autowired
    ReferralBonusConfigProperty referralBonusConfigProperty;

    @Autowired
    WalletApi walletApi;


    @Autowired
    public ReferralBonusServiceImpl(List<ReferralBonusStrategy> strategyList, UserApi userApi, ReferralBonusRepository bonusRepository) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(s -> s.getClass().getAnnotation(Component.class).value(), s -> s));
        this.userApi = userApi;
        this.bonusRepository = bonusRepository;
    }

    /**
     * Evaluates and applies the referral bonus if the referee is eligible based on the given strategy.
     *
     * @param referrer    The user who referred (parent).
     * @param referee     The user who was referred (child).
     * @param strategyKey The key identifying which bonus strategy to use.
     */
    private void evaluateBonus(UserInfo referrer, UserInfo referee, String strategyKey) {
        log.info("Evaluating referral bonus. Referrer ID: {}, Referee ID: {}, Strategy Key: {}", referrer.getId(), referee.getId(), strategyKey);
        ReferralBonusStrategy strategy = strategies.get(strategyKey);

        if (strategy == null) {
            log.warn("No referral strategy found for key: {}. Bonus evaluation skipped.", strategyKey);
            return;
        }

        if (strategy.isEligible(referee)) {
            log.info("Referee ID: {} is eligible for referral bonus under strategy: {}. Applying bonus.", referee.getId(), strategyKey);
            strategy.applyBonus(referrer, referee);
        } else {
            log.info("Referee ID: {} is not eligible for referral bonus under strategy: {}.", referee.getId(), strategyKey);
        }
    }



    /**
     * Evaluates a pending referral bonus for a newly registered user (referee).
     * <p>
     * This method checks if there's a pending referral bonus associated with the referee.
     * If found, it fetches both the referrer and referee's user info and evaluates the bonus
     * according to the associated trigger strategy.
     *
     * @param refereeId The ID of the user who triggered the referral condition (newly registered user).
     */
    @Override
    //    @Audit(action = "EVALUATE_BONUS")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void approvePendingBonus(Long refereeId, BigDecimal depositAmount) {
        log.info("📥 Starting referral bonus approval | refereeId={}", refereeId);

        // Attempt to find a pending bonus associated with this referee
        Optional<ReferralBonus> optional = bonusRepository.findByRefereeIdAndStatus(refereeId, BonusStatus.PENDING);

        if (optional.isEmpty()) {
            log.warn("⚠️ No pending referral bonus found | refereeId={}", refereeId);
            return;
        }

        ReferralBonus bonus = optional.get();
        Long referrerId = bonus.getReferrerId();
        TriggerType triggerType = bonus.getTriggerType();

        // 1. Calculate Bonus Amount
        BigDecimal bonusAmount = calculateReferralBonus(depositAmount, referrerId, refereeId, triggerType);
        if (bonusAmount == null || bonusAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("⛔ Bonus amount is invalid. Skipping wallet update for referrerId: {}, refereeId: {}", referrerId, refereeId);
            return;
        }

        // 2. Update bonus status
        bonus.setStatus(BonusStatus.APPROVED);
        bonus.setRemarks(Remarks.REFERRAL_BONUS);
        bonusRepository.save(bonus);
        log.info("🔄 Bonus status updated to APPROVED | bonusId={}", bonus.getId());

        // 3. Record referral income
        incomeHistoryService.recordIncomeEntry(
                referrerId,
                bonusAmount,
                IncomeType.REFERRAL,
                refereeId,
                Remarks.REFERRAL_BONUS
        );

        // 4. Update Wallet Balance
        log.info("Updating the wallet for UserID: {} with referralBonus: {}", referrerId, bonusAmount);
        WalletUpdateRequest depositRequest = new WalletUpdateRequest(
                bonusAmount,
                TransactionType.REFERRAL,
                true,
                "referral-bonus",
                Remarks.REFERRAL_BONUS,
                null
        );
        walletApi.updateWalletBalance(referrerId, depositRequest);
        log.info("👛 Wallet updated successfully | userId={}, amount={}", referrerId, bonusAmount);
    }

    @Override
    public void evaluateBonus(Long refereeId) {
        log.info("Evaluating referral bonus for Referee ID: {}", refereeId);

        // Attempt to find a pending bonus associated with this referee
        Optional<ReferralBonus> optional = bonusRepository.findByRefereeIdAndStatus(refereeId, BonusStatus.PENDING);

        if (optional.isPresent()) {
            log.info("Pending referral bonus found for Referee ID: {}", refereeId);

            ReferralBonus bonus = optional.get();
            Long referrerId = bonus.getReferrerId();
            String strategy = bonus.getTriggerType().getLabel();

            // Retrieve user info for both referee and referrer
            log.info("Fetching user info for Referee ID: {} and Referrer ID: {}", refereeId, referrerId);
            UserInfo referee = userApi.getUserById(refereeId);
            UserInfo referrer = userApi.getUserById(bonus.getReferrerId());

            // Evaluate the bonus using the relevant strategy
            log.info("Evaluating bonus using strategy: {} for Referrer ID: {}, Referee ID: {}", strategy, referrerId, refereeId);
            this.evaluateBonus(referrer, referee, strategy);
        }
    }

//    @Audit(action = "EVALUATE_ALL_PENDING_BONUS")
    public void evaluateAllPendingBonuses() {
        log.info("evaluateAllPendingBonuses........");
        List<ReferralBonus> referralBonuses = bonusRepository.findByStatus(BonusStatus.PENDING);
        log.info("Total PENDING users: {}", referralBonuses.size());

        for (ReferralBonus bonus : referralBonuses) {
            log.info("Evaluating ReferralBonus for referrer: {}", bonus.getReferrerId());
            String strategyKey = bonus.getTriggerType().getLabel();
            ReferralBonusStrategy strategy = strategies.get(strategyKey);
            log.info("Evaluating ReferralBonus using strategy: {}", strategy);

            if (strategy != null) {
                boolean processed = strategy.processPendingBonus(bonus);
                if (processed) {
                    bonus.setStatus(BonusStatus.APPROVED);
                    bonus.setRemarks(Remarks.REFERRAL_BONUS);
                    log.info("Updating ReferralBonus to DB with status as: {}......", bonus.getStatus());
                    bonusRepository.save(bonus);
                }
            }
        }
    }

//    @Audit(action = "CREATE_PENDING_BONUS")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createPendingBonus(Long referrerId, Long refereeId, TriggerType triggerType) {
        if (!referralBonusConfigProperty.isEnable()) {
            log.info("Referral bonus is disabled. Skipping bonus creation for referrerId: {}, refereeId: {}, triggerType: {}", referrerId, refereeId, triggerType);
            return;
        }
        BigDecimal referralBonus = BigDecimal.ZERO;

        /*BigDecimal referralBonus;
        if (referralBonusConfigProperty.getCalculationType() == CalculationType.FLAT) {
            referralBonus = referralBonusConfigProperty.getFlatAmount();
            if (referralBonus == null || referralBonus.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("Referral bonus amount is invalid or zero (amount: {}). Skipping bonus creation for referrerId: {}, refereeId: {}, triggerType: {}",
                        referralBonus, referrerId, refereeId, triggerType);
                return;
            }
        } else if (referralBonusConfigProperty.getCalculationType() == CalculationType.PERCENTAGE) {
            referralBonus = referralBonusConfigProperty.getPercentageRate();
            if (referralBonus == null || referralBonus.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("Referral bonus percentage is invalid or zero (percentageRate: {}). Skipping bonus creation for referrerId: {}, refereeId: {}, triggerType: {}", referralBonus, referrerId, refereeId, triggerType);
                return;
            }
        } else { // Handle other CalculationTypes or set referralBonus accordingly
            referralBonus = BigDecimal.ZERO; // or throw exception/log warning
            log.warn("CalculationType {} is not supported yet. Skipping bonus creation for referrerId: {}, refereeId: {}, triggerType: {}", referralBonusConfigProperty.getCalculationType(), referrerId, refereeId, triggerType);
            return;
        }*/

        log.info("Initiating pending referral bonus creation. Referrer ID: {}, Referee ID (new user): {}, Trigger Type: {}", referrerId, refereeId, triggerType);
        ReferralBonus bonus = new ReferralBonus();
        bonus.setReferrerId(referrerId);
        bonus.setRefereeId(refereeId);
        bonus.setBonusAmount(referralBonus); // or strategy-based
        bonus.setTriggerType(triggerType);
        bonus.setStatus(BonusStatus.PENDING);

        log.info("Saving ReferralBonus to DB. Referrer ID: {}, Referee ID: {}, Amount: {}, Status: {}, Trigger Type: {}", referrerId, refereeId, bonus.getBonusAmount(), bonus.getStatus(), triggerType);
        bonus = bonusRepository.save(bonus);
        log.info("BONUS: {}", bonus);
    }

    private BigDecimal calculateReferralBonus(BigDecimal depositAmount, Long referrerId, Long refereeId, TriggerType triggerType) {
        CalculationType type = referralBonusConfigProperty.getCalculationType();

        if (type == CalculationType.FLAT) {
            BigDecimal flatAmount = referralBonusConfigProperty.getFlatAmount();
            if (flatAmount == null || flatAmount.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("Referral bonus (FLAT) is invalid or zero (amount: {}). Skipping bonus for referrerId: {}, refereeId: {}, triggerType: {}",
                        flatAmount, referrerId, refereeId, triggerType);
                return null;
            }
            return flatAmount;

        } else if (type == CalculationType.PERCENTAGE) {
            BigDecimal percentageRate = referralBonusConfigProperty.getPercentageRate();
            if (percentageRate == null || percentageRate.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("Referral bonus (PERCENTAGE) is invalid or zero (rate: {}). Skipping bonus for referrerId: {}, refereeId: {}, triggerType: {}",
                        percentageRate, referrerId, refereeId, triggerType);
                return null;
            }
            BigDecimal rateAsDecimal = percentageRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
            BigDecimal calculated = depositAmount.multiply(rateAsDecimal).setScale(2, RoundingMode.DOWN);
            log.info("Calculated referral bonus (PERCENTAGE): {} | depositAmount: {}, rate: {}, referrerId: {}, refereeId: {}",
                    calculated, depositAmount, percentageRate, referrerId, refereeId);
            return calculated;

        } else {
            log.warn("Unsupported CalculationType: {}. Skipping bonus for referrerId: {}, refereeId: {}, triggerType: {}",
                    type, referrerId, refereeId, triggerType);
            return null;
        }
    }
}
