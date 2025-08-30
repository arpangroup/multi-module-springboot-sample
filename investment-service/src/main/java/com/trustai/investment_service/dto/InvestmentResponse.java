package com.trustai.investment_service.dto;

import java.math.BigDecimal;

public record InvestmentResponse(
    Long investmentId,
    BigDecimal expectedReturnAmount
){}
