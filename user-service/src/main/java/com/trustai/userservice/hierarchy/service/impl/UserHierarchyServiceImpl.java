package com.trustai.userservice.hierarchy.service.impl;

import com.trustai.common.domain.user.User;
import com.trustai.common.repository.user.UserRepository;
import com.trustai.userservice.hierarchy.UserHierarchy;
import com.trustai.userservice.hierarchy.UserTreeNode;
import com.trustai.userservice.hierarchy.repository.UserHierarchyRepository;
import com.trustai.userservice.hierarchy.service.UserHierarchyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserHierarchyServiceImpl implements UserHierarchyService {
    private final UserHierarchyRepository hierarchyRepo;
    private final UserRepository userRepository;

//    @Audit(action = "UPDATE_USER_HIERARCHY")
    public void updateHierarchy(Long referredId, Long newUserId) {
        log.info("Updating user hierarchy for newUserId: {}, referredId: {}.......", newUserId, referredId);
        List<UserHierarchy> pathToSave = new ArrayList<>();

        // Insert direct path
        log.info("Creating direct path.....");
        UserHierarchy directPath = new UserHierarchy(referredId, newUserId, 1);
        pathToSave.add(directPath);

        // Insert paths for all ancestors of the referrer, up to a maximum depth of 3
        log.info("Creating all ancestors of the referrer.....");
        List<UserHierarchy> ancestorPaths = hierarchyRepo.findByDescendant(referredId);
        pathToSave.addAll(
                ancestorPaths.stream()
                        .filter(path -> path.getDepth() < 3) // Restrict to a maximum 3 levels
                        .map(path -> new UserHierarchy(path.getAncestor(), newUserId, path.getDepth() + 1))
                        .toList()
        );

        // Save all paths in a single batch operation
        log.info("Inserting all path to DBr.....");
        hierarchyRepo.saveAll(pathToSave);
    }

//    @Audit(action = "DOWNLINE_TREE")
    public UserTreeNode getDownlineTree(Long rootUserId, int maxLevel) {
        log.info("getDownlineTree for root userId: {}, maxLevel: {}.....", rootUserId, maxLevel);
        User root = userRepository.findById(rootUserId).orElse(null);
        if (root == null) return null;

        UserTreeNode rootNode = new UserTreeNode(root.getId(), root.getUsername(), root.getWalletBalance(), root.getRankCode());
        buildTreeRecursively(rootNode, 1, maxLevel); // max level 3
        return rootNode;
    }

//    @Audit(action = "DOWNLINES_GROUPED_BY_LEVEL")
    public Map<Integer, List<Long>> getDownlinesGroupedByLevel(Long userId) {
        log.info("getDownlinesGroupedByLevel for userId: {}.....", userId);
        List<UserHierarchy> hierarchy = hierarchyRepo.findByAncestor(userId);

        return hierarchy.stream()
                .filter(h -> h.isActive() && h.getDepth() >= 1 && h.getDepth() <= 3)
                .collect(Collectors.groupingBy(
                        UserHierarchy::getDepth,
                        Collectors.mapping(UserHierarchy::getDescendant, Collectors.toList())
                ));
    }

    @Override
    public List<UserHierarchy> findByDescendant(Long descendant) {
        return hierarchyRepo.findByDescendant(descendant);
    }


    /*public Set<Long> getDirectReferrals(Long userId, int depth) {
        return hierarchyRepo.findByAncestorAndDepth(userId, depth).stream()
                .map(UserHierarchy::getDescendant)
                .collect(Collectors.toSet());
    }*/


    /*public Set<Long> calculateRankUsers(Long baseUserId, RankLevel level, RankEvaluationContext context) {
        if (level == RankLevel.LEVEL_1) {
            return getDirectReferrals(baseUserId, 1);
        }

        // Use cached data to build higher-level ranks
        RankLevel prevLevel = level.previous();
        Set<Long> previousRankUsers = context.getUsersForLevel(prevLevel);
        Set<Long> currentRankUsers = new HashSet<>();

        for (Long userId : previousRankUsers) {
            currentRankUsers.addAll(getDirectReferrals(userId, 1));
        }

        return currentRankUsers;
    }*/


    /*public List<UserHierarchy> getDownline(Long userId) {
        return userHierarchyRepository.findByAncestor(userId).stream()
                .filter(path -> path.getDepth() <= 3)
                .collect(Collectors.toList());
    }*/



    private void buildTreeRecursively(UserTreeNode parentNode, int currentLevel, int maxLevel) {
        if (currentLevel > maxLevel) return;

        List<UserHierarchy> directChildrenPaths = hierarchyRepo.findByAncestorAndDepth(parentNode.getUserId(), 1);

        for (UserHierarchy path : directChildrenPaths) {
            Long childId = path.getDescendant();
            User childUser = userRepository.findById(childId).orElse(null);
            if (childUser != null) {
                UserTreeNode childNode = new UserTreeNode(childUser.getId(), childUser.getUsername(), childUser.getWalletBalance(), childUser.getRankCode());
                parentNode.addChild(childNode);
                buildTreeRecursively(childNode, currentLevel + 1, maxLevel);
            }
        }
    }
}
