package com.trustai.investment_service.controller;

import com.trustai.common.controller.BaseController;
import com.trustai.investment_service.dto.InvestmentRequest;
import com.trustai.investment_service.dto.InvestmentResponse;
import com.trustai.investment_service.dto.UserInvestmentSummary;
import com.trustai.investment_service.enums.InvestmentStatus;
import com.trustai.investment_service.service.InvestmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/investments")
@RequiredArgsConstructor
@Slf4j
public class InvestmentController extends BaseController {
    private final InvestmentService investmentService;

    @GetMapping
    public ResponseEntity<Page<UserInvestmentSummary>> getInvestments(
            @RequestParam(required = false) InvestmentStatus status,
            Pageable pageable
    ) {
        Long userId = getCurrentUserId(); // From token
        if (isAdmin()) {
            return ResponseEntity.ok(investmentService.getAllInvestments(status, pageable));
        } else {
            return ResponseEntity.ok(investmentService.getUserInvestments(userId, status, pageable));
        }
    }

    @GetMapping("/total")
    public ResponseEntity<Map<String, Object>> getTotalInvestmentSummary() {
        Map<String, Object> summary = Map.of(
                "totalStakeValue", "0",
                "totalStakeProfit", "0"
        );
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/{investmentId}")
    public ResponseEntity<UserInvestmentSummary> getInvestmentDetails(@PathVariable Long investmentId) {
        return ResponseEntity.ok(investmentService.getInvestmentDetails(investmentId));
    }

    @PostMapping("/subscribe")
    public ResponseEntity<InvestmentResponse> subscribe(@RequestBody @Valid InvestmentRequest request) {
        Long userId = getCurrentUserId();
        log.info("Received investment subscription request: userId={}, schemaId={}, amount={}", userId, request.getSchemaId(), request.getAmount());
        InvestmentResponse response = investmentService.subscribeToInvestment(userId, request.getSchemaId(), request.getAmount());

        log.info("Subscription successful: investmentId={}, userId={}", response.investmentId(), userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/export")
    public ResponseEntity<List<UserInvestmentSummary>> exportUserInvestments(@PathVariable Long userId) {
        return ResponseEntity.ok(investmentService.exportUserInvestments(userId));
    }
}
