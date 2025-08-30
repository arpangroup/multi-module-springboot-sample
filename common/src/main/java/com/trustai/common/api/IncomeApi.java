package com.trustai.common.api;

import com.trustai.common.dto.IncomeSummaryDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public interface IncomeApi {
    List<IncomeSummaryDto> getIncomeSummary(Long userId);
    Map<Long, BigDecimal> getUserShares(List<Long> userId, LocalDateTime startDate, LocalDateTime endDate);
}
