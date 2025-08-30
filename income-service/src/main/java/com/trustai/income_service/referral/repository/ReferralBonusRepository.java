package com.trustai.income_service.referral.repository;

import com.trustai.income_service.referral.entity.BonusStatus;
import com.trustai.income_service.referral.entity.ReferralBonus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReferralBonusRepository extends JpaRepository<ReferralBonus, Long> {
//    List<ReferralBonus> findByRefereeIdAndTriggerTypeAndEvaluatedFalse(Long refereeId, ReferralBonusTriggerType referralBonusTriggerType);
    List<ReferralBonus> findByStatus(BonusStatus status);

    Optional<ReferralBonus> findByReferrerIdAndRefereeIdAndStatus(Long referrerId, Long refereeId, BonusStatus bonusStatus);

    // Optional alias:
    @Query("SELECT rb FROM ReferralBonus rb WHERE rb.status = 'PENDING'")
    List<ReferralBonus> findAllPending();

    Optional<ReferralBonus> findByRefereeIdAndStatus(Long refereeId, BonusStatus bonusStatus);
}
