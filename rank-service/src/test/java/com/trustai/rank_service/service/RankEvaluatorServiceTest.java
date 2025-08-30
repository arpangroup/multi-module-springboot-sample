package com.trustai.rank_service.service;

import com.trustai.common.api.UserApi;
import com.trustai.common.dto.UserHierarchyStats;
import com.trustai.common.dto.UserInfo;
import com.trustai.common.dto.UserMetrics;
import com.trustai.rank_service.TestRankDataFactory;
import com.trustai.rank_service.entity.RankConfig;
import com.trustai.rank_service.evaluation.DirectReferralSpec;
import com.trustai.rank_service.evaluation.MinimumDepositAmountSpec;
import com.trustai.rank_service.evaluation.RankSpecification;
import com.trustai.rank_service.evaluation.RequiredLevelCountsSpec;
import com.trustai.rank_service.repository.RankConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


/*
✅ Existing Test Case Coverage Review
| Scenario                                 | Test Method                                  | Comments                                              |
| ---------------------------------------- | -------------------------------------------- | ----------------------------------------------------- |
| **Low-rank match (RANK\_0 only)**        | `userQualifiesForRank0Only`                  | Good. Confirms fallback works and base criteria pass. |
| **Mid-rank match (RANK\_2)**             | `userQualifiesForRank2`                      | Clear test. Good metric coverage.                     |
| **Blocked by one spec (downlines)**      | `userStuckAtRank1DueToMissingDownlineCounts` | Great scenario, simulates partial criteria success.   |
| **No rank matched, fallback to RANK\_0** | `userDoesNotMatchAnyRank`                    | Correct. Validates fallback logic.                    |
| **Highest rank match (RANK\_7)**         | `userQualifiesForHighestRank`                | Perfectly checks for top-tier qualification.          |
| **No ranks configured**                  | `evaluateReturnsEmptyWhenNoRanksAvailable`   | Important and handled well.                           |

 */
@ExtendWith(MockitoExtension.class)
public class RankEvaluatorServiceTest {

    @InjectMocks
    private RankEvaluatorServiceImpl rankEvaluatorService;

    @Mock
    private RankConfigRepository rankRepo;

    @Mock
    private UserApi userClient;

    private final List<RankSpecification> specifications = List.of(
            new DirectReferralSpec(),
            new MinimumDepositAmountSpec(),
            new RequiredLevelCountsSpec()
    );

    @BeforeEach
    void setup() {
        rankEvaluatorService = new RankEvaluatorServiceImpl(rankRepo, specifications, userClient);
    }

    @Test
    void userQualifiesForRank0Only() {
        Long userId = 1001L;
        UserInfo user = new UserInfo();
        user.setId(userId);
        user.setRankCode("RANK_0");

        UserMetrics metrics = UserMetrics.builder()
                .directReferrals(0)
                .totalDeposit(new BigDecimal("15"))  // Exactly matches RANK_0
                .userHierarchyStats(UserHierarchyStats.builder()
                        .depthWiseCounts(Map.of(1, 0L, 2, 0L, 3, 0L))
                        .build())
                .build();

        when(userClient.computeMetrics(userId)).thenReturn(metrics);
        when(rankRepo.findAllByActiveTrueOrderByRankOrderDesc()).thenReturn(TestRankDataFactory.getAllRanks());

        Optional<RankConfig> result = rankEvaluatorService.evaluate(user);

        assertTrue(result.isPresent());
        assertEquals("RANK_0", result.get().getCode());
    }

    @Test
    void userQualifiesForRank2() {
        Long userId = 1002L;
        UserInfo user = new UserInfo();
        user.setId(userId);
        user.setRankCode("RANK_0");

        UserMetrics metrics = UserMetrics.builder()
                .directReferrals(4)
                .totalDeposit(new BigDecimal("500"))
                .userHierarchyStats(UserHierarchyStats.builder()
                        .depthWiseCounts(Map.of(1, 4L, 2, 5L, 3, 1L))
                        .build())
                .build();

        when(userClient.computeMetrics(userId)).thenReturn(metrics);
        when(rankRepo.findAllByActiveTrueOrderByRankOrderDesc()).thenReturn(TestRankDataFactory.getAllRanks());

        Optional<RankConfig> result = rankEvaluatorService.evaluate(user);

        assertTrue(result.isPresent());
        assertEquals("RANK_2", result.get().getCode());
    }

    @Test
    void userStuckAtRank1DueToMissingDownlineCounts() {
        Long userId = 1003L;
        UserInfo user = new UserInfo();
        user.setId(userId);
        user.setRankCode("RANK_0");

        UserMetrics metrics = UserMetrics.builder()
                .directReferrals(5)
                .totalDeposit(new BigDecimal("1000"))
                .userHierarchyStats(UserHierarchyStats.builder()
                        .depthWiseCounts(Map.of(1, 2L, 2, 2L, 3, 0L))
                        .build())
                .build();

        when(userClient.computeMetrics(userId)).thenReturn(metrics);
        when(rankRepo.findAllByActiveTrueOrderByRankOrderDesc()).thenReturn(TestRankDataFactory.getAllRanks());

        Optional<RankConfig> result = rankEvaluatorService.evaluate(user);

        assertTrue(result.isPresent());
        assertEquals("RANK_1", result.get().getCode());
    }

    @Test
    void userDoesNotMatchAnyRank() {
        Long userId = 1004L;
        UserInfo user = new UserInfo();
        user.setId(userId);
        user.setRankCode("RANK_0");

        UserMetrics metrics = UserMetrics.builder()
                .directReferrals(0)
                .totalDeposit(new BigDecimal("5")) // Too low to match any real rank
                .userHierarchyStats(UserHierarchyStats.builder()
                        .depthWiseCounts(Map.of())
                        .build())
                .build();

        when(userClient.computeMetrics(userId)).thenReturn(metrics);
        when(rankRepo.findAllByActiveTrueOrderByRankOrderDesc()).thenReturn(TestRankDataFactory.getAllRanks());

        Optional<RankConfig> result = rankEvaluatorService.evaluate(user);

        assertTrue(result.isPresent(), "Expected fallback to RANK_0");
        assertEquals("RANK_0", result.get().getCode());
    }

    @Test
    void userQualifiesForHighestRank() {
        Long userId = 1005L;
        UserInfo user = new UserInfo();
        user.setId(userId);
        user.setRankCode("RANK_5");

        UserMetrics metrics = UserMetrics.builder()
                .directReferrals(40)
                .totalDeposit(new BigDecimal("20000"))
                .userHierarchyStats(UserHierarchyStats.builder()
                        .depthWiseCounts(Map.of(1, 40L, 2, 400L, 3, 60L))
                        .build())
                .build();

        when(userClient.computeMetrics(userId)).thenReturn(metrics);
        when(rankRepo.findAllByActiveTrueOrderByRankOrderDesc()).thenReturn(TestRankDataFactory.getAllRanks());

        Optional<RankConfig> result = rankEvaluatorService.evaluate(user);

        assertTrue(result.isPresent());
        assertEquals("RANK_7", result.get().getCode()); // Highest valid rank based on sample config
    }

    @Test
    void evaluateReturnsEmptyWhenNoRanksAvailable() {
        Long userId = 2001L;
        UserInfo user = new UserInfo();
        user.setId(userId);
        user.setRankCode("RANK_0");

        UserMetrics metrics = UserMetrics.builder()
                .directReferrals(1)
                .totalDeposit(BigDecimal.TEN)
                .userHierarchyStats(UserHierarchyStats.builder()
                        .depthWiseCounts(Map.of())
                        .build())
                .build();

        // Simulate no active ranks configured
        when(userClient.computeMetrics(userId)).thenReturn(metrics);
        when(rankRepo.findAllByActiveTrueOrderByRankOrderDesc()).thenReturn(Collections.emptyList());

        Optional<RankConfig> result = rankEvaluatorService.evaluate(user);

        assertTrue(result.isEmpty(), "Expected empty result when no ranks are available");
    }

    /* ################################################## */

    @Test
    void userAlreadyHasHigherRankShouldNotDowngrade() { // ❗ User Already Has Higher Rank (No Downgrade)
        Long userId = 1010L;
        UserInfo user = new UserInfo();
        user.setId(userId);
        user.setRankCode("RANK_5");

        UserMetrics metrics = UserMetrics.builder()
                .directReferrals(1) // No longer meets RANK_5
                .totalDeposit(BigDecimal.TEN)
                .userHierarchyStats(UserHierarchyStats.builder()
                        .depthWiseCounts(Map.of())
                        .build())
                .build();

        when(userClient.computeMetrics(userId)).thenReturn(metrics);
        when(rankRepo.findAllByActiveTrueOrderByRankOrderDesc()).thenReturn(TestRankDataFactory.getAllRanks());

        Optional<RankConfig> result = rankEvaluatorService.evaluate(user);

        assertTrue(result.isPresent());
        assertEquals("RANK_5", result.get().getCode()); // Ensure no downgrade
    }

    @Test
    void metricsIsNullReturnsEmpty() { // ❗ Null or Missing UserMetrics
        Long userId = 1011L;
        UserInfo user = new UserInfo();
        user.setId(userId);
        user.setRankCode("RANK_0");

        when(userClient.computeMetrics(userId)).thenReturn(null);
        //when(rankRepo.findAllByActiveTrueOrderByRankOrderDesc()).thenReturn(TestRankDataFactory.getAllRanks());

        Optional<RankConfig> result = rankEvaluatorService.evaluate(user);

        assertTrue(result.isEmpty(), "Should return empty when metrics are null");
    }
}
