package com.trustai.investment_service.reservation.controller;

import com.trustai.common.controller.BaseController;
import com.trustai.investment_service.dto.EligibleInvestmentSummary;
import com.trustai.investment_service.reservation.service.ReservationEligibilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations/eligibility")
@RequiredArgsConstructor
public class ReservationEligibilityController extends BaseController {
    private final ReservationEligibilityService mappingService;

    @GetMapping
    public ResponseEntity<List<EligibleInvestmentSummary>> getEligibleInvestmentSummaries() {
        Long userId = getCurrentUserId();
        List<EligibleInvestmentSummary> result = mappingService.getEligibleInvestmentSummaries(userId);
        return ResponseEntity.ok(result);
    }
}
