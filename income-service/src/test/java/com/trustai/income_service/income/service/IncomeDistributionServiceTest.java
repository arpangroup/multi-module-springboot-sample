/*
package com.trustai.income_service.income.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trustai.common.api.RankConfigApi;
import com.trustai.common.api.UserApi;
import com.trustai.common.api.WalletApi;
import com.trustai.common.dto.RankConfigDto;
import com.trustai.common.dto.UserHierarchyDto;
import com.trustai.common.dto.UserInfo;
import com.trustai.common.dto.WalletUpdateRequest;
import com.trustai.common.enums.IncomeType;
import com.trustai.income_service.income.entity.IncomeHistory;
import com.trustai.income_service.income.repository.IncomeHistoryRepository;
import com.trustai.income_service.income.repository.TeamIncomeConfigRepository;
import com.trustai.income_service.income.strategy.TeamIncomeStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IncomeDistributionServiceTest {

    @InjectMocks
    private IncomeDistributionService incomeService;

    @Mock
    private IncomeHistoryRepository incomeRepo;
    @Mock
    private TeamIncomeConfigRepository teamIncomeRepo;
    @Mock
    private TeamIncomeStrategy teamIncomeStrategy;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private UserApi userClient;
    @Mock
    private RankConfigApi rankConfigClient;
    @Mock
    private WalletApi walletClient;

    @Test
    void testDistributeIncome_shouldDistributeDailyAndTeamIncomeProperly() throws JsonProcessingException {
        // Given
        Long sellerId = 101L;
        BigDecimal saleAmount = new BigDecimal("500.00");
        String sellerRank = "RANK_3";

        UserInfo seller = new UserInfo();
        seller.setId(sellerId);
        seller.setRankCode(sellerRank);

        RankConfigDto configDto = new RankConfigDto();
        configDto.setCode(sellerRank);
        configDto.setCommissionPercentage(new BigDecimal("6")); // 6%

        List<UserHierarchyDto> hierarchyDtos = List.of(
                new UserHierarchyDto(1L, 80L, 101L, 1, true), // upline 80 is 1 level above 101
                new UserHierarchyDto(2L, 55L, 101L, 2, true)  // upline 55 is 2 levels above 101
        );

        List<UserInfo> uplines = List.of(
                new UserInfo(80L, "RANK_5"),
                new UserInfo(55L, "RANK_3")
        );

        IncomeHistory savedIncome = IncomeHistory.builder()
                .userId(sellerId)
                .amount(new BigDecimal("30.00"))
                .incomeType(IncomeType.DAILY)
                .sourceUserId(sellerId)
                .sourceUserRank(sellerRank)
                .note("Self income")
                .build();

        when(userClient.getUserById(sellerId)).thenReturn(seller);
        when(rankConfigClient.getRankConfigByRankCode(sellerRank)).thenReturn(configDto);
        when(incomeRepo.save(any(IncomeHistory.class))).thenReturn(savedIncome);
        when(userClient.findByDescendant(sellerId)).thenReturn(hierarchyDtos);
        when(userClient.getUsers(anyList())).thenReturn(uplines);
        when(objectMapper.writeValueAsString(any())).thenReturn("{json}");
        when(teamIncomeStrategy.distributeTeamIncome(eq(sellerId), eq(sellerRank), any(), eq(uplines), any()))
                .thenReturn(List.of());

        // When
        incomeService.distributeIncome(sellerId, saleAmount);

        // Then
        verify(incomeRepo).save(argThat(income ->
                income.getUserId().equals(sellerId)
                        && income.getIncomeType() == IncomeType.DAILY
                        && income.getAmount().compareTo(new BigDecimal("30.00")) == 0
        ));

        verify(walletClient).updateWalletBalance(eq(sellerId), any(WalletUpdateRequest.class));
        verify(teamIncomeStrategy).distributeTeamIncome(eq(sellerId), eq(sellerRank), eq(new BigDecimal("30.00")), eq(uplines), anyMap());
    }
}
*/
