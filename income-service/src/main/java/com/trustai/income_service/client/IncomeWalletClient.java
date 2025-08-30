/*
package com.trustai.income_service.client;

import com.trustai.common.api.WalletApi;
import com.trustai.common.config.FeignConfig;
import com.trustai.common.constants.CommonConstants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = CommonConstants.WALLET_SERVICE,
        url = CommonConstants.BASE_URL,
        path = CommonConstants.PATH_WALLET_SERVICE,
        configuration = FeignConfig.class
)
@ConditionalOnProperty(name = "feign.client.enabled.income", havingValue = "true")
public interface IncomeWalletClient extends WalletApi {
}
*/
