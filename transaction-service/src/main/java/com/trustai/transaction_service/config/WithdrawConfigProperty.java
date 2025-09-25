package com.trustai.transaction_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationProperties(prefix = "withdraw.config")
@Getter
@Setter
public class WithdrawConfigProperty {
    //@Value("${withdraw.config.amount.min:10}")
    private BigDecimal amountMin;
    //@Value("${withdraw.config.service.charge.percentage:0.05}")
    private BigDecimal serviceChargePercentage;
    //@Value("${withdraw.config.service.charge.fixed:2.0}")
    private BigDecimal serviceChargeFixed;
    //@Value("${withdraw.config.service.charge.threshold:10}")
    private BigDecimal serviceChargeThreshold;
}
