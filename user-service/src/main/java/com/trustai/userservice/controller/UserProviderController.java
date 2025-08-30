package com.trustai.userservice.controller;

import com.trustai.common.domain.user.User;
import com.trustai.common.dto.UserInfo;
import com.trustai.common.dto.UserMetrics;
import com.trustai.common.repository.user.UserRepository;
import com.trustai.userservice.hierarchy.UserHierarchy;
import com.trustai.userservice.hierarchy.repository.UserHierarchyRepository;
import com.trustai.userservice.hierarchy.service.MemberSummaryService;
import com.trustai.userservice.user.exception.IdNotFoundException;
import com.trustai.userservice.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/provider")
@RequiredArgsConstructor
@Slf4j
public class UserProviderController {
    private final UserRepository userRepository;
    private final UserHierarchyRepository userHierarchyRepository;
    private final MemberSummaryService memberSummaryService;
    private final UserMapper mapper;

    @GetMapping("/users")
    public List<UserInfo> getUsers() {
        log.info("Received request to get all users");
        List<UserInfo> users = userRepository.findAll().stream().map(mapper::mapTo).toList();
        log.info("Returning {} users", users.size());
        return users;
    }

    @GetMapping("/users/activeIds")
    public List<Long> getAllActiveUserIds() {
        log.info("Received request to get all active user ids");
        List<User> users = userRepository.findByAccountStatus(User.AccountStatus.ACTIVE);
        List<Long> ids = users.stream().map(User::getId).toList();
        log.info("Returning {} users", ids.size());
        return ids;
    }

    @PostMapping("/users/by-ids")
    public List<UserInfo> getUserByIds(@RequestBody List<Long> ids) {
        log.info("Received request to get users by IDs: {}", ids);
        List<UserInfo> users = userRepository.findByIdIn(ids).stream().map(mapper::mapTo).toList();
        log.info("Returning {} users for requested IDs", users.size());
        return users;
    }

    @GetMapping("/users/{userId}")
    public UserInfo getUserById(@PathVariable Long userId) {
        log.info("Received request to get user by ID: {}", userId);
        return userRepository.findById(userId)
                .map(user -> {
                    log.info("User found for ID: {}", userId);
                    return mapper.mapTo(user);
                })
                .orElseThrow(() -> {
                    log.error("User not found for ID: {}", userId);
                    return new IdNotFoundException("userId: " + userId + " not found");
                });
    }

    @PutMapping("/users/{userId}/rank")
    public void updateRank(@PathVariable Long userId, @RequestBody String rankCode) {
        log.info("Received request to update rank for userId: {} with rankCode: {}", userId, rankCode);
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("User not found for ID: {}", userId);
            return new IdNotFoundException("userId: " + userId + " not found");
        });
        user.setRankCode(rankCode);
        userRepository.save(user);
        log.info("Updated rankCode to '{}' for userId: {}", rankCode, userId);
    }


    @PutMapping("/users/{userId}/wallet-balance")
    public void updateWalletBalance(@PathVariable Long userId, @RequestBody BigDecimal updatedNewBalance) {
        log.info("Received request to update wallet balance for userId: {} with new balance: {}", userId, updatedNewBalance);

        // First, check if the user exists
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("User not found for ID: {}", userId);
            return new IdNotFoundException("userId: " + userId + " not found");
        });

        // Update the wallet balance on the user entity
        user.setWalletBalance(updatedNewBalance);

        // Persist the change
        userRepository.save(user);

        log.info("Successfully updated wallet balance to {} for userId: {}", updatedNewBalance, userId);
    }

    @GetMapping("/users/{userId}/metrics")
    public UserMetrics computeMetrics(@PathVariable Long userId) {
        log.info("findByDescendant for userId: {}", userId);
        return memberSummaryService.computeMetrics(userId);
    }

    @GetMapping("/hierarchy/descendant/{descendant}")
    public List<UserHierarchy> findByDescendant(@PathVariable Long descendant) {
        log.info("Received request to get user hierarchy for descendant: {}", descendant);
        List<UserHierarchy> hierarchy = userHierarchyRepository.findByDescendant(descendant);
        log.info("Returning {} hierarchy records for descendant: {}", hierarchy.size(), descendant);
        return hierarchy;
    }

}
