package com.trustai.transaction_service.config;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Component
@ConfigurationProperties(prefix = "withdraw.config")
@Validated
@Getter
@Setter
public class WithdrawConfigProperty {
    @NotNull
    //@Value("${withdraw.config.amount.min:10}")
    private BigDecimal amountMin;

    @NotNull
    //@Value("${withdraw.config.service.charge.percentage:0.05}")
    private BigDecimal serviceChargePercentage;

    @NotNull
    //@Value("${withdraw.config.service.charge.fixed:2.0}")
    private BigDecimal serviceChargeFixed;

    @NotNull
    //@Value("${withdraw.config.service.charge.threshold:10}")
    private BigDecimal serviceChargeThreshold;
}
