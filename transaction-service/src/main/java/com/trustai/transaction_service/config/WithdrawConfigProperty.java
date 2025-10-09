package com.trustai.transaction_service.config;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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

    //@NotNull
    //@Value("${withdraw.config.max-withdraw-attempts-per-rank}")
    //private Integer maxWithdrawAttemptsPerRank = 1; // default once per rank

    @NotNull
    //@Value("${withdraw.config.withdraw-limit-by-rank=RANK_0=0.5,RANK_1=0.5,RANK_2=0.5,RANK_3=1.0,RANK_4=1.0,RANK_5=1.0,RANK_6=1.0,RANK_7=1.0}")
    /*
    withdraw:
        config:
            withdraw-limit-by-rank:
              RANK_1: 0.5
              RANK_1: 0.5
              RANK_2: 0.5
              RANK_3: 1.0
              RANK_4: 1.0
              RANK_5: 1.0
              RANK_6: 1.0
              RANK_4: 7.0
     */
    private String withdrawLimitByRank;

    /**
     * Parsed map: rankCode -> withdraw percentage
     */
    private Map<String, BigDecimal> withdrawLimitByRankMap = new HashMap<>();


    @PostConstruct
    public void init() {
        if (withdrawLimitByRank != null && !withdrawLimitByRank.isEmpty()) {
            String[] entries = withdrawLimitByRank.split(",");
            for (String entry : entries) {
                String[] kv = entry.split("=");
                if (kv.length == 2) {
                    withdrawLimitByRankMap.put(kv[0].trim(), new BigDecimal(kv[1].trim()));
                }
            }
        }
    }
}
