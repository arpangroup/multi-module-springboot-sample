package com.trustai.userservice.hierarchy.service.impl;

import com.trustai.common.domain.user.User;
import com.trustai.common.repository.user.UserRepository;
import com.trustai.userservice.hierarchy.UserHierarchy;
import com.trustai.userservice.hierarchy.UserTreeNode;
import com.trustai.userservice.hierarchy.repository.UserHierarchyRepository;
import com.trustai.userservice.hierarchy.service.UserHierarchyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

//@Service
//@Primary
@RequiredArgsConstructor
@Slf4j
public class UserHierarchyServiceImplV1 implements UserHierarchyService {
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

    public UserTreeNode getDownlineTree(Long rootUserId, int maxLevel) {
        // Step 1: Get all users in the subtree (including root)
        // Step 1: Get all users in subtree up to maxLevel
        List<UserHierarchy> subtreePaths = hierarchyRepo.findByAncestorAndDepthLessThanEqual(rootUserId, maxLevel);
        if (subtreePaths.isEmpty()) return null;

        Set<Long> allUserIds = subtreePaths.stream()
                .map(UserHierarchy::getDescendant)
                .collect(Collectors.toSet());
        allUserIds.add(rootUserId); // include root

        Map<Long, User> userMap = userRepository.findAllById(allUserIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        // Step 2: Get all depth=1 edges between any two nodes in the subtree
        List<UserHierarchy> edges = hierarchyRepo.findDepth1EdgesInSubtree(rootUserId, maxLevel);

        // Step 3: Build parent → child map
        Map<Long, List<Long>> childrenMap = new HashMap<>();
        for (UserHierarchy edge : edges) {
            childrenMap
                    .computeIfAbsent(edge.getAncestor(), k -> new ArrayList<>())
                    .add(edge.getDescendant());
        }

        // Step 4: Build tree recursively
        return buildTreeRecursive(rootUserId, userMap, childrenMap, 1, maxLevel);
    }

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
        return List.of();
    }

    @Override
    public void activateUserHierarchy(Long userId) {

    }

    private UserTreeNode buildTreeRecursive(Long userId, Map<Long, User> userMap, Map<Long, List<Long>> childrenMap, int currentLevel, int maxLevel) {
        User user = userMap.get(userId);
        if (user == null || currentLevel > maxLevel) return null;

        UserTreeNode node = new UserTreeNode(user.getId(), user.getUsername(), user.getWalletBalance(), user.getRankCode());

        List<Long> childrenIds = childrenMap.getOrDefault(userId, Collections.emptyList());
        for (Long childId : childrenIds) {
            UserTreeNode childNode = buildTreeRecursive(childId, userMap, childrenMap, currentLevel + 1, maxLevel);
            if (childNode != null) {
                node.addChild(childNode);
            }
        }

        return node;
    }
}
