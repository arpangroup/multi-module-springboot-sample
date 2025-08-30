package com.trustai.transaction_service.service;

import com.trustai.transaction_service.entity.Transaction;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;

public interface BonusTransactionService {

    /**
     * Applies a one-time signup bonus to a new user's account.
     * <p>
     * This method credits a predefined bonus amount to the user's wallet or account
     * as part of a promotional or onboarding campaign. It is typically called
     * immediately after account creation or upon meeting certain signup criteria.
     *
     * @param userId       the ID of the newly registered user receiving the bonus.
     *                     This identifies the account to which the bonus will be credited.
     *
     * @param bonusAmount  the bonus amount to be credited (must be non-null and positive).
     *                     This value may be fixed or configured externally as part of business rules.
     *
     * @return a {@link Transaction} object representing the bonus credit,
     *         including amount, timestamp, and transaction type.
     *
     * @throws IllegalArgumentException if the user is not eligible for a signup bonus
     *         or if the bonus has already been applied.
     */
    Transaction applySignupBonus(long userId, @NonNull BigDecimal bonusAmount);


    /**
     * Credits a referral bonus to the referrer when a referred user successfully registers or qualifies.
     * <p>
     * This method awards a bonus amount to the referrer as part of a referral program,
     * incentivizing users to invite others to the platform.
     *
     * @param referrerUserId  the ID of the user who referred another user and will receive the bonus.
     *                        This user’s account will be credited with the referral bonus.
     *
     * @param referredUserId  the ID of the new user who was referred.
     *                        This is used to track and validate the referral relationship.
     *
     * @param bonusAmount     the bonus amount to be credited to the referrer (must be non-null and positive).
     *                        The value is typically configured as part of the referral program rules.
     *
     * @return a {@link Transaction} object representing the referral bonus transaction,
     *         including the credited amount and transaction metadata.
     *
     * @throws IllegalArgumentException if the referral relationship is invalid or the bonus has already been granted.
     */
    Transaction applyReferralBonus(long referrerUserId, long referredUserId, @NonNull BigDecimal bonusAmount);


    /**
     * Applies a general bonus to a user's account.
     * <p>
     * This method credits a specified bonus amount to the user's balance for various purposes,
     * such as promotions, campaigns, adjustments, or rewards.
     *
     * @param userId       the ID of the user receiving the bonus.
     *                     Identifies the account to be credited.
     *
     * @param bonusAmount  the bonus amount to be credited (must be non-null and positive).
     *                     Represents the value of the bonus being applied.
     *
     * @param reason       a descriptive reason or note explaining why the bonus is being applied.
     *                     Useful for audit trails and reporting.
     *
     * @return a {@link Transaction} object representing the bonus credit transaction,
     *         including details such as amount, timestamp, and reason.
     *
     * @throws IllegalArgumentException if the bonusAmount is invalid or the reason is missing.
     */
    Transaction applyBonus(long userId, @NonNull BigDecimal bonusAmount, String reason);


    /**
     * Applies interest earnings to a user's account.
     * <p>
     * This method credits the specified interest amount to the user's balance,
     * typically calculated based on the user's holdings over a defined period.
     *
     * @param userId            the ID of the user receiving the interest.
     *                          Identifies the account to be credited.
     *
     * @param interestAmount    the amount of interest to be credited (must be non-null and positive).
     *                          Represents the interest earned for the specified period.
     *
     * @param periodDescription a textual description of the interest period (e.g., "Monthly June 2025", "Q1 2025").
     *                          Helps provide context for reporting and audit purposes.
     *
     * @return a {@link Transaction} object representing the interest credit transaction,
     *         including details such as amount, period, and timestamp.
     *
     * @throws IllegalArgumentException if interestAmount is invalid or periodDescription is null/empty.
     */
    Transaction applyInterest(long userId, @NonNull BigDecimal interestAmount, String periodDescription);

}
