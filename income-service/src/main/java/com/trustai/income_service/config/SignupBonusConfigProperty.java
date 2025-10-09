package com.trustai.income_service.config;

import com.trustai.common.enums.CalculationType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationProperties(prefix = "bonus.signup")
@RefreshScope
@Getter
@Setter
public class SignupBonusConfigProperty {
    private boolean enable;
    private CalculationType calculationType;
    private BigDecimal flatAmount;
}
