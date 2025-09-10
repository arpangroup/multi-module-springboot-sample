package com.trustai.investment_service.dto;

import com.trustai.investment_service.entity.InvestmentSchema;
import com.trustai.investment_service.enums.InvestmentType;
import com.trustai.investment_service.enums.ReturnType;
import com.trustai.investment_service.enums.SchemaType;
import com.trustai.investment_service.validation.annotations.EnumValue;
import com.trustai.investment_service.validation.annotations.ValidRangeAmounts;
import com.trustai.investment_service.validation.annotations.ValidStakeSchema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@ValidStakeSchema
@ValidRangeAmounts
public class SchemaRequest {
    //@Pattern(regexp = "STANDARD|STAKE|PROMO", message = "Investment subtype must be one of: STANDARD, STAKE")
    @NotNull(message = "investmentType must not be null")
    @EnumValue(enumClass = InvestmentType.class, message = "investmentType must be one of: STANDARD, STAKE, PROMO")
    @NotBlank(message = "investmentType is required")
    private String investmentType; // ["STANDARD", "STAKE"]

    @NotBlank(message = "Schema name must not be blank")
    @Size(min = 4, message = "Schema name must be at least 4 characters long")
    private String name;

    private String schemaBadge;
    private String imageUrl;

    //@Pattern(regexp = "FIXED|RANGE", message = "Schema type must be either FIXED or RANGE")
    @EnumValue(enumClass = SchemaType.class, message = "schemaType must be one of: FIXED, RANGE")
    @NotBlank(message = "Schema type is required")
    private String schemaType; // ["FIXED", "RANGE"]
    private BigDecimal minimumInvestmentAmount;
    private BigDecimal maximumInvestmentAmount;
    private BigDecimal stakePrice; // only for STAKE


    //@Pattern(regexp = "PERIOD|LIFETIME", message = "Return type must be either PERIOD or LIFETIME")
    @EnumValue(enumClass = ReturnType.class, message = "returnType must be one of: PERIOD, LIFETIME, FIXED")
    @NotBlank(message = "Return type is required")
    private String returnType; // ["PERIOD", "LIFETIME"]
    private BigDecimal returnRate;
    private Integer totalReturnPeriods;
    private BigDecimal handlingFee;
    private BigDecimal minimumWithdrawalAmount;
    private Long returnScheduleId;


    private String currency;
    private boolean isCancellable;
    private BigDecimal earlyExitPenalty;
    private int cancellationGracePeriodMinutes;

    private boolean isCapitalReturned;
    private boolean isFeatured;
    private boolean isTradeable;
    private boolean isActive;
    private String description;
    private String termsAndConditionsUrl;
}
