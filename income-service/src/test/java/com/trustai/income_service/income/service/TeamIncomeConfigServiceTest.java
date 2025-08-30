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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeamIncomeConfigServiceTest {

    @InjectMocks
    private TeamIncomeConfigService service;

    @Mock
    private TeamIncomeConfigRepository repo;

    @Test
    void shouldReturnAllConfigs() {
        TeamIncomeConfig config = new TeamIncomeConfig(new TeamIncomeKey("RANK_1", 1), BigDecimal.TEN);
        when(repo.findAll()).thenReturn(List.of(config));

        List<TeamIncomeConfig> result = service.getAll();
        assertEquals(1, result.size());
        assertEquals("RANK_1", result.get(0).getId().getUplineRank());
    }

    @Test
    void shouldUpdateOrInsertConfigs() {
        TeamIncomeKey key = new TeamIncomeKey("RANK_1", 1);
        TeamIncomeConfig newConfig = new TeamIncomeConfig(key, BigDecimal.TEN);
        TeamIncomeConfig existingConfig = new TeamIncomeConfig(key, BigDecimal.ONE); // existing value is different

        when(repo.findById(key)).thenReturn(Optional.of(existingConfig));

        service.updateTeamConfigs(List.of(newConfig));

        // Ensure the payout percentage was updated
        assertEquals(BigDecimal.TEN, existingConfig.getPayoutPercentage());

        verify(repo).save(existingConfig);
    }
}
