//package com.trustai.income_service.income.service;
//
//import com.trustai.common.enums.IncomeType;
//import com.trustai.income_service.income.dto.UserIncomeSummary;
//import com.trustai.income_service.income.entity.IncomeHistory;
//import com.trustai.income_service.income.repository.IncomeHistoryRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.when;
//
///*
//🔍 Test Inputs:
//Mocked repository return from incomeRepo.findIncomeSummaryByUserId(userId, ...):
//| IncomeType | DailyIncome | TotalIncome |
//| ---------- | ----------- | ----------- |
//| DAILY      | 5.00        | 50.00       |
//| DAILY      | 7.50        | 60.00       |
//| TEAM       | 2.00        | 30.00       |
//
//⚙️ Expected Behavior:
// */
//@ExtendWith(MockitoExtension.class)
//public class IncomeHistoryServiceTest {
//
//    @InjectMocks
//    private IncomeHistoryService service;
//
//    @Mock
//    private IncomeHistoryRepository incomeRepo;
//
//    @Test
//    void shouldReturnIncomeSummaryWithDefaultValues() {
//        Object[] row1 = {IncomeType.DAILY, new BigDecimal("10.50"), new BigDecimal("100.00")};
//        Object[] row2 = {IncomeType.TEAM, new BigDecimal("2.00"), new BigDecimal("30.00")};
//
//        when(incomeRepo.findIncomeSummaryByUserId(anyLong(), any()))
//                .thenReturn(List.of(row1, row2));
//
//        List<UserIncomeSummary> result = service.getUserIncomeSummary(1L);
//
//        assertEquals(3, result.size());
//
//        assertEquals("Comprehensive", result.get(0).type());
//        assertEquals(new BigDecimal("10.50"), result.get(0).dailyIncome());
//        assertEquals(new BigDecimal("100.00"), result.get(0).totalIncome());
//
//        assertEquals("Reserve", result.get(1).type());
//        assertEquals(new BigDecimal("1.30"), result.get(1).dailyIncome().setScale(2, RoundingMode.HALF_UP));
//        assertEquals(new BigDecimal("1.30"), result.get(1).totalIncome().setScale(2, RoundingMode.HALF_UP));
//
//        assertEquals("Team", result.get(2).type());
//        assertEquals(new BigDecimal("2.00"), result.get(2).dailyIncome());
//        assertEquals(new BigDecimal("30.00"), result.get(2).totalIncome());
//    }
//
//    @Test
//    void shouldReturnAggregatedIncomeSummaryIncludingReserveAndDefaults() {
//        // Given: 2 DAILY records and 1 TEAM record
//        Object[] dailyRow1 = {IncomeHistory.IncomeType.DAILY, new BigDecimal("5.00"), new BigDecimal("50.00")};
//        Object[] dailyRow2 = {IncomeHistory.IncomeType.DAILY, new BigDecimal("7.50"), new BigDecimal("60.00")};
//        Object[] teamRow = {IncomeHistory.IncomeType.TEAM, new BigDecimal("2.00"), new BigDecimal("30.00")};
//
//        when(incomeRepo.findIncomeSummaryByUserId(anyLong(), any()))
//                .thenReturn(List.of(dailyRow1, dailyRow2, teamRow));
//
//        // When
//        List<UserIncomeSummary> result = service.getUserIncomeSummary(123L);
//
//        // Then
//        assertEquals(3, result.size());
//
//        // 1. DAILY → "Comprehensive", 5.00 + 7.50 = 12.50, 50.00 + 60.00 = 110.00
//        UserIncomeSummary comprehensive = result.get(0);
//        assertEquals("Comprehensive", comprehensive.type());
//        assertEquals(new BigDecimal("12.50"), comprehensive.dailyIncome());
//        assertEquals(new BigDecimal("110.00"), comprehensive.totalIncome());
//
//        // 2. Hardcoded "Reserve" → 1.30 fixed
//        UserIncomeSummary reserve = result.get(1);
//        assertEquals("Reserve", reserve.type());
//        assertEquals(new BigDecimal("1.30"), reserve.dailyIncome());
//        assertEquals(new BigDecimal("1.30"), reserve.totalIncome());
//
//        // 3. TEAM → "Team", 2.00 daily, 30.00 total
//        UserIncomeSummary team = result.get(2);
//        assertEquals("Team", team.type());
//        assertEquals(new BigDecimal("2.00"), team.dailyIncome());
//        assertEquals(new BigDecimal("30.00"), team.totalIncome());
//    }
//}
