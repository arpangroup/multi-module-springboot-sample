package com.trustai.income_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationProperties(prefix = "bonus.signup")
@Getter
@Setter
public class SignupBonusConfig {
    private boolean enable;
    private BigDecimal flatAmount;
}
