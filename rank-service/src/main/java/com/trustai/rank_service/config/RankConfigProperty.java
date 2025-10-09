package com.trustai.rank_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "investment.rank")
@RefreshScope
@Getter
@Setter
public class RankConfigProperty {
    private boolean preventDowngrade = true;        // @Value("${investment.rank.prevent-downgrade:true}")
    private boolean preferHighestQualified = true;  // @Value("${investment.rank.prefer-highest-qualified:true}")
}
