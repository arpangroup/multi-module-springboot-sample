package com.trustai.investment_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@ToString
public class SchemaUpsertRequest {
    private Long id;
    private String linkedRankCode;
    private BigDecimal minimumInvestmentAmount;
    private BigDecimal maximumInvestmentAmount;
    private BigDecimal returnRate;
    private Integer totalReturnPeriods;
    private Long returnScheduleId;
    private Boolean capitalReturned;
    private Boolean active;
}
