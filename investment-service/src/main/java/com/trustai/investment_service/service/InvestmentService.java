package com.trustai.investment_service.service;

import com.trustai.investment_service.dto.InvestmentResponse;
import com.trustai.investment_service.dto.UserInvestmentSummary;
import com.trustai.investment_service.enums.InvestmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface InvestmentService {
    InvestmentResponse subscribeToInvestment(Long userId, Long schemaId, BigDecimal amount);
    Page<UserInvestmentSummary> getAllInvestments(InvestmentStatus status, Pageable pageable);
    Page<UserInvestmentSummary> getUserInvestments(Long userId, InvestmentStatus status, Pageable pageable);
    List<UserInvestmentSummary> exportUserInvestments(Long userId);
    UserInvestmentSummary getInvestmentDetails(Long investmentId);
    UserInvestmentSummary cancelInvestment(Long userId, Long investmentId);
}
