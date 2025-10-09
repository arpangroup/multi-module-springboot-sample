package com.trustai.investment_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InvestmentResponse(
        Long investmentId,
        BigDecimal expectedReturnAmount,
        LocalDateTime maturityAt
){}
