package com.trustai.investment_service.reservation.service;

import com.trustai.investment_service.dto.EligibleInvestmentSummary;

import java.util.List;

public interface ReservationEligibilityService {
    List<EligibleInvestmentSummary> getEligibleInvestmentSummaries(Long userId);
}
