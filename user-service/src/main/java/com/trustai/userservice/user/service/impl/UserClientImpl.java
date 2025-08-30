package com.trustai.userservice.user.service.impl;/*
package com.trustai.userservice.user.service.impl;

import com.trustai.common.client.UserClient;
import com.trustai.common.dto.UserHierarchyDto;
import com.trustai.common.dto.UserInfo;
import com.trustai.userservice.hierarchy.UserHierarchy;
import com.trustai.userservice.hierarchy.service.UserHierarchyService;
import com.trustai.userservice.user.mapper.UserMapper;
import com.trustai.userservice.user.service.UserProfileService;
import com.trustai.userservice.user.service.UserBalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserClientImpl implements UserClient {
    private final UserProfileService profileService;
    private final UserBalanceService userBalanceService;
    private final UserHierarchyService userHierarchyService;
    private final UserMapper mapper;

    @Override
    public List<UserInfo> getUsers() {
        log.debug("Fetching all users");
        return profileService.getUsers().stream().map(mapper::mapTo).toList();
    }

    @Override
    public List<UserInfo> getUsers(List<Long> userIds) {
        log.debug("Fetching users with IDs: {}", userIds);
        return profileService.getUserByIds(userIds).stream().map(mapper::mapTo).toList();
    }

    @Override
    public UserInfo getUserById(Long userId) {
        log.debug("Fetching user by ID: {}", userId);
        var user = profileService.getUserById(userId);
        return mapper.mapTo(user);
    }

    @Override
    public String getRankCode(Long userId) {
        log.debug("Fetching rank code for userId: {}", userId);
        String rankCode =  profileService.getUserById(userId).getRankCode();
        log.info("Rank code for userId {}: {}", userId, rankCode);
        return rankCode;
    }

    @Override
    public void updateRank(Long userId, String newRankCode) {
        log.info("Updating rank for userId: {} to new rank: {}", userId, newRankCode);
        profileService.updateUserRank(userId, newRankCode);
        log.debug("Rank updated for userId: {}", userId);
    }

    @Override
    public Optional<BigDecimal> findWalletBalanceById(Long userId) {
        log.debug("Fetching wallet balance for userId: {}", userId);
        Optional<BigDecimal> balance =  userBalanceService.findWalletBalanceById(userId);
        log.info("Wallet balance for userId {}: {}", userId, balance.orElse(null));
        return balance;
    }

    @Override
    public void updateWalletBalance(long userId, BigDecimal updatedAmount) {
        log.info("Updating wallet balance for userId: {} to {}", userId, updatedAmount);
        userBalanceService.updateWalletBalance(userId, updatedAmount);
        log.debug("Wallet balance update complete for userId: {}", userId);
    }

    @Override
    public List<UserHierarchyDto> findByDescendant(Long descendant) {
        List<UserHierarchy> userHierarchies = userHierarchyService.findByDescendant(descendant);

        List<UserHierarchyDto> list = new ArrayList<>();
        for (UserHierarchy h : userHierarchies) {
            UserHierarchyDto dto = new UserHierarchyDto(h.getId(), h.getAncestor(), h.getDescendant(), h.getDepth(), h.isActive());
            list.add(dto);
        }
        return list;
    }

    */
/*@Override
    public Optional<BigDecimal> findDepositBalanceById(Long userId) {
        return userBalanceService.findDepositBalanceById(userId);
    }

    @Override
    public void updateDepositBalance(long userId, BigDecimal updatedTotalDepositAmount) {
        userBalanceService.updateDepositBalance(userId, updatedTotalDepositAmount);
    }*//*


}
*/
