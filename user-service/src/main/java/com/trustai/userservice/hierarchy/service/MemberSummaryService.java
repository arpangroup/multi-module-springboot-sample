package com.trustai.userservice.hierarchy.service;

import com.trustai.common.api.IncomeApi;
import com.trustai.common.api.TransactionApi;
import com.trustai.common.domain.user.User;
import com.trustai.common.dto.UserHierarchyStats;
import com.trustai.common.dto.UserMetrics;
import com.trustai.common.repository.user.UserRepository;
import com.trustai.userservice.hierarchy.UserHierarchy;
import com.trustai.userservice.hierarchy.repository.UserHierarchyRepository;
import com.trustai.userservice.user.dto.ContributionMemberDto;
import com.trustai.userservice.user.dto.MemberSummaryResponse;
import com.trustai.userservice.user.exception.IdNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberSummaryService {
    private final UserHierarchyRepository hierarchyRepo;
    private final UserRepository userRepository;
    //private final DepositService depositService;
    private final TransactionApi transactionClient;
    private final IncomeApi incomeApi;

    public UserMetrics computeMetrics(Long userId) {
        log.info("Computing user metrics for userId={}", userId);

        List<UserHierarchy> downlines = hierarchyRepo.findByAncestor(userId);
        log.debug("Found {} downlines for userId={}", downlines.size(), userId);

        User user = userRepository.findById(userId).orElseThrow(() -> new IdNotFoundException("userId " + userId + " not found"));
        log.debug("Loaded user details for userId={}", userId);

        // Aggregate team counts by depth
        Map<Integer, Long> depthCounts = downlines.stream()
                .filter(uh -> uh.getDepth() > 0)
                .collect(Collectors.groupingBy(
                        UserHierarchy::getDepth,
                        Collectors.counting()
                ));
        log.debug("Depth-wise team counts for userId={}: {}", userId, depthCounts);

        long teamSize = depthCounts.values().stream().mapToLong(Long::longValue).sum();

        UserHierarchyStats stats = UserHierarchyStats.builder()
                .depthWiseCounts(depthCounts)
                .totalTeamSize(teamSize)
                .build();

        BigDecimal totalDeposit = transactionClient.getDepositBalance(userId);
        log.info("Total deposit for userId={}: {}", userId, totalDeposit);

        //BigDecimal totalDeposit = user.getDepositBalance();
        UserMetrics metrics =  UserMetrics.builder()
                .directReferrals(depthCounts.getOrDefault(1, 0L).intValue())
                .userHierarchyStats(stats)
                .totalDeposit(totalDeposit == null ? BigDecimal.ZERO : totalDeposit)
                .totalInvestment(BigDecimal.ZERO)
                .walletBalance(user.getWalletBalance() != null ? user.getWalletBalance() : BigDecimal.ZERO)
                .totalEarnings(BigDecimal.ZERO)
                .build();

        log.info("User metrics computed for userId={}: {}", userId, metrics);
        return metrics;
    }

    /*public UserMetricsV1 computeMetricsV1(Long rootUserId, LocalDateTime start, LocalDateTime end) {
        // Step1: Fetch all descendants up to depth 3
        List<UserHierarchy> allDownlines = hierarchyRepo.findByAncestorAndDepthLessThanEqual(rootUserId, 3); // depth → userIds

        // Group them by depth
        Map<Integer, List<Long>> downlinesByLevel  = allDownlines.stream()
                .collect(Collectors.groupingBy(
                        UserHierarchy::getDepth,
                        Collectors.mapping(UserHierarchy::getDescendant, Collectors.toList())
                ));

        // Flatten all IDs
        List<Long> allIds = downlinesByLevel.values().stream().flatMap(List::stream).toList();

        // Fetch users
        List<User> users = userRepository.findByIdsAndDateRange(allIds, start, end);


        UserMetricsV1 summary = new UserMetricsV1();

        summary.setTotalUser(allIds.size());
        summary.setTotalActive((int) users.stream().filter(User::isActive).count());

        summary.setDirect(downlinesByLevel.getOrDefault(1, List.of()).size());
        summary.setActiveDirect(countActive(users, downlinesByLevel.getOrDefault(1, List.of())));

        summary.setIndirect(downlinesByLevel.getOrDefault(2, List.of()).size());
        summary.setActiveIndirect(countActive(users, downlinesByLevel.getOrDefault(2, List.of())));

        summary.setThird(downlinesByLevel.getOrDefault(3, List.of()).size());
        summary.setActiveThird(countActive(users, downlinesByLevel.getOrDefault(3, List.of())));

        //summary.setTotalShare(incomeApi.sumTeamShare(allIds, start, end));
        summary.setTotalShare(shareMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add));

        return summary;
    }*/

    public MemberSummaryResponse getMemberSummary(Long rootUserId, LocalDateTime start, LocalDateTime end) {
        // Step1: Fetch all descendants up to depth 3
        List<UserHierarchy> allDownlines = hierarchyRepo.findByAncestorAndDepthLessThanEqual(rootUserId, 3); // depth → userIds

        // Group them by depth
        Map<Integer, List<Long>> downlinesByLevel  = allDownlines.stream()
                .collect(Collectors.groupingBy(
                        UserHierarchy::getDepth,
                        Collectors.mapping(UserHierarchy::getDescendant, Collectors.toList())
                ));

        // Flatten all IDs
        List<Long> allIds = downlinesByLevel.values().stream().flatMap(List::stream).toList();

        // Step2: Fetch all users in one shot
        List<User> users = userRepository.findByIdsAndDateRange(allIds, start, end);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, u -> u));

        // Step3: Fetch share amounts (from IncomeHistory or Reservation table) in one query
        Map<Long, BigDecimal> shareMap = incomeApi.getUserShares(allIds, start, end);

        // Step4: Prepare / Process  contribution details
        List<ContributionMemberDto> contributions = new ArrayList<>();
        BigDecimal memberA = BigDecimal.ZERO;
        BigDecimal memberB = BigDecimal.ZERO;
        BigDecimal memberC = BigDecimal.ZERO;

        for (int level = 1; level <= 3; level++) {
            List<Long> idsAtLevel = downlinesByLevel.getOrDefault(level, List.of());

            for (Long id : idsAtLevel) {
                User user = userMap.get(id);
                if (user == null) continue;

                BigDecimal share = shareMap.getOrDefault(id, BigDecimal.ZERO);
                String relation = switch (level) {
                    case 1 -> "A";
                    case 2 -> "B";
                    case 3 -> "C";
                    default -> "?";
                };

                contributions.add(ContributionMemberDto.builder()
                        .userId(user.getId())
                        .username(user.getUsername())
                        .name(user.getFullName())  // Adjust if needed
                        .related(relation)
                        .share(share)
                        .build());

                switch (level) {
                    case 1 -> memberA = memberA.add(share);
                    case 2 -> memberB = memberB.add(share);
                    case 3 -> memberC = memberC.add(share);
                }
            }
        }

        Set<Long> filteredIds = new HashSet<>(userMap.keySet());
        /*return MemberSummaryResponse.builder()
                .totalUser(filteredIds.size()) // allIds.size() is wrong
                .totalActive((int) users.stream().filter(User::isActive).count())

                .direct(downlinesByLevel.getOrDefault(1, List.of()).size())
                .activeDirect(countActive(users, downlinesByLevel.getOrDefault(1, List.of())))

                .indirect(downlinesByLevel.getOrDefault(2, List.of()).size())
                .activeIndirect(countActive(users, downlinesByLevel.getOrDefault(2, List.of())))

                .third(downlinesByLevel.getOrDefault(3, List.of()).size())
                .activeThird(countActive(users, downlinesByLevel.getOrDefault(3, List.of())))

                .totalShare(memberA.add(memberB).add(memberC))
                .memberA(memberA)
                .memberB(memberB)
                .memberC(memberC)

                .memembers(contributions)
                .build();*/

        return MemberSummaryResponse.builder()
                .totalUser(filteredIds.size())
                .totalActive((int) users.stream().filter(User::isActive).count())

                .direct((int) downlinesByLevel.getOrDefault(1, List.of()).stream().filter(filteredIds::contains).count())
                .activeDirect(countActive(users, downlinesByLevel.getOrDefault(1, List.of())))

                .indirect((int) downlinesByLevel.getOrDefault(2, List.of()).stream().filter(filteredIds::contains).count())
                .activeIndirect(countActive(users, downlinesByLevel.getOrDefault(2, List.of())))

                .third((int) downlinesByLevel.getOrDefault(3, List.of()).stream().filter(filteredIds::contains).count())
                .activeThird(countActive(users, downlinesByLevel.getOrDefault(3, List.of())))

                .totalShare(memberA.add(memberB).add(memberC))
                .memberA(memberA)
                .memberB(memberB)
                .memberC(memberC)

                .memembers(contributions)
                .build();

    }


    private int countActive(List<User> users, List<Long> ids) {
        /*return (int) users.stream()
                .filter(u -> ids.contains(u.getId()) && u.isActive())
                .count();*/
        Set<Long> idSet = new HashSet<>(ids);
        return (int) users.stream()
                .filter(u -> idSet.contains(u.getId()) && u.isActive())
                .count();
    }
}
