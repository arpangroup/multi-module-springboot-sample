package com.trustai.income_service.income.service;

import com.trustai.common.enums.IncomeType;
import com.trustai.income_service.income.dto.UserIncomeSummary;
import com.trustai.income_service.income.entity.IncomeHistory;
import com.trustai.income_service.income.entity.IncomeSummaryProjection;
import com.trustai.income_service.income.repository.IncomeHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IncomeHistoryService {
    private final IncomeHistoryRepository incomeHistoryRepository;

    public List<IncomeSummaryProjection> getIncomeSummary(@Nullable Long userId) {
        //return incomeHistoryRepository.getIncomeSummary();
        List<IncomeSummaryProjection> rawList = incomeHistoryRepository.getIncomeSummary(userId);

        // Step 1: Map result by IncomeType
        Map<IncomeType, IncomeSummaryProjection> map = rawList.stream()
                .collect(Collectors.toMap(
                        IncomeSummaryProjection::getIncomeType,
                        Function.identity()
                ));

        // Step 2: Build full list
        List<IncomeSummaryProjection> completeList = new ArrayList<>();
        for (IncomeType type : IncomeType.values()) {
            if (map.containsKey(type)) {
                completeList.add(map.get(type));
            } else {
                // Add a default zero-value projection
                completeList.add(new IncomeSummaryProjection() {
                    public IncomeType getIncomeType() { return type; }
                    public BigDecimal getTodayAmount() { return BigDecimal.ZERO; }
                    public BigDecimal getYesterdayAmount() { return BigDecimal.ZERO; }
                    public BigDecimal getLast7DaysAmount() { return BigDecimal.ZERO; }
                    public BigDecimal getTotalAmount() { return BigDecimal.ZERO; }
                    public Long getTotalOrders() { return 0L; }
                    public Long getProcessingOrders() { return 0L; }
                });
            }
        }

        return completeList;
    }

    /*
        BigDecimal totalShare = incomeHistoryService.sumShare(allDownlineIds, startDate, endDate);
        BigDecimal level1Share = incomeHistoryService.sumShare(level1Ids, startDate, endDate);
        BigDecimal level2Share = incomeHistoryService.sumShare(level2Ids, startDate, endDate);
        BigDecimal level3Share = incomeHistoryService.sumShare(level3Ids, startDate, endDate);
    */
    public Map<Long, BigDecimal> getUserShares(List<Long> userIds, @Nullable LocalDateTime start, @Nullable LocalDateTime end) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Object[]> results = incomeHistoryRepository.sumSharesByUserIdsAndDateRange(userIds, start, end);

        return results.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (BigDecimal) row[1]
                ));
    }

    public Page<IncomeHistory> getIncomeDetails(
            @Nullable Long userId,
            @Nullable LocalDateTime startDate,
            @Nullable LocalDateTime endDate,
            @Nullable IncomeType incomeType,
            Pageable pageable
    ) {
        return incomeHistoryRepository.findByFilters(userId, startDate, endDate, incomeType, pageable);
    }
}
