/*
package com.trustai.mlm_rank_service.config;

import com.trustai.mlm_rank_service.evaluation.MinimumDepositAmountSpec;
import com.trustai.mlm_rank_service.evaluation.DirectReferralSpec;
import com.trustai.mlm_rank_service.evaluation.RankSpecification;
import com.trustai.mlm_rank_service.evaluation.RequiredLevelCountsSpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RankModuleConfig {

    @Bean
    public List<RankSpecification> rankSpecifications(
            MinimumDepositAmountSpec depositSpec,
            DirectReferralSpec directReferralSpec,
            RequiredLevelCountsSpec levelCountsSpec
    ) {
        return List.of(
                depositSpec,
                directReferralSpec,
                levelCountsSpec
        );
    }

    @Bean
    public MinimumDepositAmountSpec depositSpec() {
        return new MinimumDepositAmountSpec();
    }

    @Bean
    public DirectReferralSpec directReferralSpec() {
        return new DirectReferralSpec();
    }

    @Bean
    public RequiredLevelCountsSpec levelCountsSpec() {
        return new RequiredLevelCountsSpec();
    }
}
*/
