/*
package com.trustai.rank_service.scheduler;

import com.trustai.common.api.UserApi;
import com.trustai.common.dto.UserInfo;
import com.trustai.rank_service.service.RankCalculationOrchestrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;

public class DailyRankEvaluationJobTest {

    @Mock
    private UserApi userApi;

    @Mock
    private RankCalculationOrchestrationService orchestrationService;

    @InjectMocks
    private DailyRankEvaluationJob rankEvaluationJob;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void evaluateRanks_shouldCallOrchestrationForAllUsers() {
        // Arrange
        UserInfo user1 = new UserInfo();
        user1.setId(1L);
        user1.setRankCode("RANK_1");

        UserInfo user2 = new UserInfo();
        user2.setId(2L);
        user2.setRankCode("RANK_2");

        when(userApi.getUsers()).thenReturn(List.of(user1, user2));

        // Act
        rankEvaluationJob.evaluateRanks();

        // Assert
        verify(userApi, times(1)).getUsers();
        verify(orchestrationService, times(1)).reevaluateRank(1L, "DAILY_SCHEDULED_JOB", "");
        verify(orchestrationService, times(1)).reevaluateRank(2L, "DAILY_SCHEDULED_JOB", "");
    }

    @Test
    void evaluateRanks_shouldHandleExceptionGracefully() {
        // Arrange
        when(userApi.getUsers()).thenThrow(new RuntimeException("Simulated error"));

        // Act
        rankEvaluationJob.evaluateRanks();

        // Assert
        verify(userApi, times(1)).getUsers();
        verifyNoInteractions(orchestrationService); // should not call reevaluate if failed early
    }
}
*/
