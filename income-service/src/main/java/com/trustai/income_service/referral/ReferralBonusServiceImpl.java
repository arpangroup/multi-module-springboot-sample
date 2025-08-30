package com.trustai.income_service.referral;

import com.trustai.common.api.UserApi;
import com.trustai.common.dto.UserInfo;
import com.trustai.common.enums.TriggerType;
import com.trustai.income_service.constant.Remarks;
import com.trustai.income_service.referral.entity.BonusStatus;
import com.trustai.income_service.referral.entity.ReferralBonus;
import com.trustai.income_service.referral.repository.ReferralBonusRepository;
import com.trustai.income_service.referral.service.ReferralBonusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReferralBonusServiceImpl implements ReferralBonusService {
    private final UserApi userApi;
    private final Map<String, ReferralBonusStrategy> strategies;
    private final ReferralBonusRepository bonusRepository;

    @Autowired
    public ReferralBonusServiceImpl(List<ReferralBonusStrategy> strategyList, UserApi userApi, ReferralBonusRepository bonusRepository) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(s -> s.getClass().getAnnotation(Component.class).value(), s -> s));
        this.userApi = userApi;
        this.bonusRepository = bonusRepository;
    }

    private void evaluateBonus(UserInfo referrer, UserInfo referee, String strategyKey) {
        log.info("evaluateBonus for Referrer ID: {}, Referee ID: {}, Strategy: {}", referrer.getId(), referee.getId(), strategyKey);
        ReferralBonusStrategy strategy = strategies.get(strategyKey);
        if (strategy != null && strategy.isEligible(referee)) {
            log.info("Apply Referral Bonus........");
            strategy.applyBonus(referrer, referee);
        }
    }

//    @Audit(action = "EVALUATE_BONUS")
    public void evaluateBonus(Long refereeId) {
        log.info("evaluateBonus for refereeId: {}", refereeId);
        // Find the bonus for this user & trigger type
        Optional<ReferralBonus> optional = bonusRepository.findByRefereeIdAndStatus(refereeId, BonusStatus.PENDING);

        if (optional.isPresent()) {
            log.info("PENDING record found for Referee ID: {}", refereeId);
            ReferralBonus bonus = optional.get();
            String strategy = bonus.getTriggerType().getLabel();

            // Load referrer and referee data (via API or shared module)
            log.info("Calling userClient to get userInfo for Referee ID: {} and Referrer ID: {}", refereeId, bonus.getReferrerId());
            UserInfo referee = userApi.getUserById(refereeId);
            UserInfo referrer = userApi.getUserById(bonus.getReferrerId());

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
    public void createPendingBonus(Long referrerId, Long refereeId, TriggerType triggerType) {
        log.info("creatingPendingBonus for referrerId: {}, refereeId: {}, triggerType: {}........", referrerId, refereeId, triggerType);
        ReferralBonus bonus = new ReferralBonus();
        bonus.setReferrerId(referrerId);
        bonus.setRefereeId(refereeId);
        bonus.setBonusAmount(BigDecimal.valueOf(100)); // or strategy-based
        bonus.setTriggerType(triggerType);
        bonus.setStatus(BonusStatus.PENDING);
        bonus.setCreatedAt(LocalDateTime.now());

        log.info("Creating ReferralBonus to DB for referrerId: {}, refereeId: {} with status as: {}....", referrerId, refereeId, bonus.getStatus());
        bonusRepository.save(bonus);
    }
}
