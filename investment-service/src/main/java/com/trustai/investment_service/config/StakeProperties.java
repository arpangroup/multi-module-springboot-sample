package com.trustai.investment_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationProperties(prefix = "investment.stake")
@Getter
@Setter
public class StakeProperties {
    private BigDecimal valuationDelta = BigDecimal.valueOf(5); // @Value("${investment.stake.valuation-delta:5}")
}
