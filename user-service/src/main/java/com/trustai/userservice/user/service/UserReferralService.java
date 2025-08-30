package com.trustai.userservice.user.service;


import com.trustai.common.domain.user.User;

import java.util.List;

public interface UserReferralService {
    User getUserById(Long userId);
    List<User> getUserByIds(List<Long> userIds);
    User getUserByReferralCode(String referralCode);
    void approveReferralBonus(Long userId);
}
