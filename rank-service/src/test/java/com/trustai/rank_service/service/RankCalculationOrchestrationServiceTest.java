/*
package com.trustai.rank_service.service;

import com.trustai.common.api.UserApi;
import com.trustai.common.dto.UserInfo;
import com.trustai.rank_service.entity.RankConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;

public class RankCalculationOrchestrationServiceTest {

    private RankEvaluatorServiceImpl rankEvaluatorService;
    private UserApi userApi;
    private RankCalculationOrchestrationService orchestrationService;

    @BeforeEach
    void setUp() {
        rankEvaluatorService = mock(RankEvaluatorServiceImpl.class);
        userApi = mock(UserApi.class);
        orchestrationService = new RankCalculationOrchestrationService(rankEvaluatorService, userApi);
    }

    @Test
    void reevaluateRank_updatesRankWhenDifferent() {
        Long userId = 1L;
        String currentRank = "RANK_1";
        String newRank = "RANK_2";

        UserInfo user = new UserInfo();
        user.setId(userId);
        user.setRankCode(currentRank);

        RankConfig newRankConfig = new RankConfig();
        newRankConfig.setCode(newRank);

        when(userApi.getUserById(userId)).thenReturn(user);
        when(rankEvaluatorService.evaluate(user)).thenReturn(Optional.of(newRankConfig));

        orchestrationService.reevaluateRank(userId, "DAILY_SCHEDULE", "");

        verify(userApi).updateRank(userId, newRank);
    }

    @Test
    void reevaluateRank_doesNotUpdateWhenSameRank() {
        Long userId = 2L;
        String rank = "RANK_1";

        UserInfo user = new UserInfo();
        user.setId(userId);
        user.setRankCode(rank);

        RankConfig newRankConfig = new RankConfig();
        newRankConfig.setCode(rank); // same rank

        when(userApi.getUserById(userId)).thenReturn(user);
        when(rankEvaluatorService.evaluate(user)).thenReturn(Optional.of(newRankConfig));

        orchestrationService.reevaluateRank(userId, "MANUAL_TRIGGER", "");

        verify(userApi, never()).updateRank(anyLong(), anyString());
    }

    @Test
    void reevaluateRank_doesNothingWhenRankNotMatched() {
        Long userId = 3L;
        UserInfo user = new UserInfo();
        user.setId(userId);
        user.setRankCode("RANK_0");

        when(userApi.getUserById(userId)).thenReturn(user);
        when(rankEvaluatorService.evaluate(user)).thenReturn(Optional.empty());

        orchestrationService.reevaluateRank(userId, "EVENT_X", "");

        verify(userApi, never()).updateRank(anyLong(), anyString());
    }
}
*/
