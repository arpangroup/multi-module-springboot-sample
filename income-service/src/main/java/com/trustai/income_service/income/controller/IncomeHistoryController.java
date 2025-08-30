package com.trustai.income_service.income.controller;

import com.trustai.common.controller.BaseController;
import com.trustai.common.enums.IncomeType;
import com.trustai.income_service.income.entity.IncomeHistory;
import com.trustai.income_service.income.entity.IncomeSummaryProjection;
import com.trustai.income_service.income.service.IncomeHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/incomes")
@RequiredArgsConstructor
public class IncomeHistoryController extends BaseController {
    private final IncomeHistoryService incomeHistoryService;

    // 1. Income History of specific user
    @GetMapping
    public ResponseEntity<Page<IncomeHistory>> getIncomeHistory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) IncomeType incomeType,
            Pageable pageable
    ) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(incomeHistoryService.getIncomeDetails(userId, startDate, endDate, incomeType, pageable));
    }


    // 2. Income summary for multiple user (for internal services)
    @PostMapping("/user-shares")
    public ResponseEntity<Map<Long, BigDecimal>> getSumShare(
            @RequestBody List<Long> userIds,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime endDate
    ) {
        Map<Long, BigDecimal> userShares = incomeHistoryService.getUserShares(userIds, startDate, endDate);
        return ResponseEntity.ok(userShares);
    }


    // 3. Summary
    @GetMapping("/summary")
    public ResponseEntity<List<IncomeSummaryProjection>> getIncomeSummary() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(incomeHistoryService.getIncomeSummary(userId));
    }

}
