package com.trustai.income_service.referral.entity;

import com.trustai.common.enums.TriggerType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * referrerId (inviter - Existing user ):
 *      The existing user who shared the referral link/code.
 *      Example: Alice invites Bob to join — Alice is the referrer.
 *
 * refereeId:
 *      The newly registered user who was referred.
 *      Example: Bob signs up using Alice’s code — Bob is the referee.
 *
 *
 * | Field        | Who it Represents       | Purpose                                |
 * | ------------ | ----------------------- | -------------------------------------- |
 * | `referrerId` | Existing user (inviter) | Gets the bonus                         |
 * | `refereeId`  | New user (invitee)      | Triggers the bonus (upon deposit/etc.) |
 *
 *
 */
@Entity
@Table(name = "referral_bonus")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReferralBonus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long referrerId;  // ID of the user who should receive the bonus. The existing user who shared the referral link/code.

    @Column(unique = true, nullable = false) // refereeId should be unique
    private Long refereeId;   // ID of the newly referred user (the one who triggers the bonus).

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal bonusAmount; // How much bonus is to be given if approved.

    @Enumerated(EnumType.STRING)
    private TriggerType triggerType; // Enum indicating what action should trigger this bonus (e.g., FIRST_DEPOSIT, ACCOUNT_ACTIVATION).

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BonusStatus status = BonusStatus.PENDING;

    private String remarks; // Optional explanation for rejection or approval reason.

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime evaluatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
