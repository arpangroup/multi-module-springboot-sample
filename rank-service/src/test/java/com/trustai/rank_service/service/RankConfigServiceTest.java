/*
package com.trustai.rank_service.service;

import com.trustai.rank_service.dto.RankConfigDto;
import com.trustai.rank_service.entity.RankConfig;
import com.trustai.rank_service.exception.RankNotFoundException;
import com.trustai.rank_service.repository.RankConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RankConfigServiceTest {
    @Mock
    private RankConfigRepository rankConfigRepository;

    @InjectMocks
    private RankConfigService rankConfigService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllRankConfigsReturnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        List<RankConfig> rankList = List.of(new RankConfig());
        Page<RankConfig> page = new PageImpl<>(rankList);

        when(rankConfigRepository.findAll(pageable)).thenReturn(page);

        Page<RankConfig> result = rankConfigService.getAllRankConfigs(pageable);

        assertEquals(1, result.getContent().size());
        verify(rankConfigRepository).findAll(pageable);
    }

    @Test
    void getRankByIdFound() {
        RankConfig rank = new RankConfig();
        rank.setId(1L);

        when(rankConfigRepository.findById(1L)).thenReturn(Optional.of(rank));

        RankConfig result = rankConfigService.getRankById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getRankByIdNotFoundThrowsException() {
        when(rankConfigRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> rankConfigService.getRankById(1L));
        assertEquals("Id Not found", ex.getMessage());
    }

    @Test
    void getRankByRankCodeFound() {
        RankConfig rank = new RankConfig();
        rank.setCode("RANK_1");

        when(rankConfigRepository.findByCode("RANK_1")).thenReturn(Optional.of(rank));

        RankConfig result = rankConfigService.getRankByRankCode("RANK_1");

        assertEquals("RANK_1", result.getCode());
    }

    @Test
    void getRankByRankCodeNotFoundThrowsException() {
        when(rankConfigRepository.findByCode("RANK_X")).thenReturn(Optional.empty());

        assertThrows(RankNotFoundException.class, () -> rankConfigService.getRankByRankCode("RANK_X"));
    }

    @Test
    void createRankSavesNewRank() {
        RankConfigDto dto = RankConfigDto.builder()
                .code("R1")
                .displayName("Gold")
                .rankOrder(1)
                .minDirectReferrals(3)
                .minDepositAmount(BigDecimal.TEN)
                .minLevel1Count(3)
                .minLevel2Count(2)
                .minLevel3Count(1)
                .active(true)
                .build();

        ArgumentCaptor<RankConfig> captor = ArgumentCaptor.forClass(RankConfig.class);
        when(rankConfigRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RankConfig saved = rankConfigService.createRank(dto);

        verify(rankConfigRepository).save(captor.capture());
        assertEquals("R1", saved.getCode());
        assertEquals(3, saved.getRequiredLevelCounts().get(1));
    }

    @Test
    void patchRankUpdatesFieldsCorrectly() {
        RankConfig rank = new RankConfig();
        rank.setId(1L);

        Map<String, Object> updates = Map.of(
                "code", "R2",
                "minDirectReferrals", 5,
                "minDepositAmount", "500.0"
        );

        when(rankConfigRepository.findById(1L)).thenReturn(Optional.of(rank));
        when(rankConfigRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        rankConfigService.patchRank(1L, updates);

        assertEquals("R2", rank.getCode());
        assertEquals(5, rank.getMinDirectReferrals());
        assertEquals(new BigDecimal("500.0"), rank.getMinDepositAmount());
    }

    @Test
    void patchRankThrowsWhenIdNotFound() {
        when(rankConfigRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> rankConfigService.patchRank(2L, Map.of("code", "R9")));
        assertEquals("Rank not found", ex.getMessage());
    }
}
*/
