package com.trustai.userservice.user.service;


import com.trustai.common.domain.user.User;
import com.trustai.common.dto.UserInfo;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface UserProfileService {
    User createUser(User user, String referralCode);
    User updateUser(Long userId, Map<String, Object> fieldsToUpdate);
    User updateUser(User user);
    User updateUserRank(Long userId, String newRankCode);
    List<User> getUsers();
    List<User> getUserByIds(List<Long> userIds);
    List<User> getUsers(User.AccountStatus status);
    Page<UserInfo> getUsers(User.AccountStatus status, Integer page, Integer size);
    User getUserById(Long userId);
    boolean updatePassword(Long userId, String oldPassword, String newPassword);


    /**
     * This method is required for bonus-service to apply various bonus
     */
    User deposit(final long userId, final BigDecimal amount, String remarks, String metaInfo);

    /**
     * Checks whether the user has made at least one deposit transaction.
     * <p>
     * This method is used by the {@code BonusService} to determine the eligibility
     * of a user for referral bonus distribution. It ensures that referral bonuses are only
     * granted for genuine users who have made a real deposit.
     *
     * <p><strong>Why this check is important:</strong>
     * <ul>
     *   <li>If a deposit transaction exists, the referral bonus can be safely applied to the referrer.</li>
     *   <li>If no deposit exists, the system prevents awarding referral bonuses to avoid abuse from fake signups.</li>
     *   <li>A user must complete at least one deposit to trigger referral bonus eligibility.</li>
     * </ul>
     *
     * <p>The complete referral bonus logic is implemented within the {@code BonusService}.
     *
     * @param userId the ID of the user to check for deposit history
     * @return {@code true} if the user has made at least one deposit; {@code false} otherwise
     */
    boolean hasDeposit(Long userId);

    BigDecimal getTotalDeposit(Long userId);

//    boolean iasActive(Long userId);
//    void handleDeposit(Long userId, double amount);
//    void activateAccount(Long userId);
//    void checkMinimumRequirements(User user);
//    void approveReferralBonus(Long userId);
}
