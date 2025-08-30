package com.trustai.income_service.income.service;

import com.trustai.income_service.income.entity.TeamIncomeConfig;
import com.trustai.income_service.income.entity.TeamIncomeKey;
import com.trustai.income_service.income.repository.TeamIncomeConfigRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeamCommissionServiceTest {
    @InjectMocks
    private TeamCommissionService service;

    @Mock
    private TeamIncomeConfigRepository repo;

    @Test
    void shouldReturnCorrectPercentage_whenRankAndDepthExist() {
        String rank = "RANK_3";
        int depth = 1;
        BigDecimal rawPercentage  = new BigDecimal("12.00"); // stored in DB

        TeamIncomeKey key = new TeamIncomeKey(rank, depth);
        TeamIncomeConfig config = new TeamIncomeConfig(key, rawPercentage);

        when(repo.findById_UplineRankAndId_DownlineDepth(rank, depth)).thenReturn(Optional.of(config));

        BigDecimal result = service.getPercentage(rank, depth);

        // 12% = 0.12
        assertEquals(new BigDecimal("0.12"), result);
    }

    @Test
    void shouldReturnZero_whenDepthNotInMap() {
        String rank = "RANK_3";
        int depth = 5;

        when(repo.findById_UplineRankAndId_DownlineDepth(rank, depth)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () ->
                service.getPercentage(rank, depth)
        );
    }

    @Test
    void shouldThrowException_whenRankNotFound() {
        String unknownRank = "UNKNOWN";
        int depth = 2;

        when(repo.findById_UplineRankAndId_DownlineDepth(unknownRank, depth)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalStateException.class, () ->
                service.getPercentage(unknownRank, depth)
        );

        assertTrue(exception.getMessage().contains("No team config found for UplineRank: UNKNOWN and DownlineDepth: 2"));
    }
}
