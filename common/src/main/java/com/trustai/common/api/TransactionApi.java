package com.trustai.common.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

/*@FeignClient(
        name = "IncomeService.TransactionClient",
        url = CommonConstants.BASE_URL,
        path = CommonConstants.PATH_TRANSACTION_SERVICE,
        configuration = FeignConfig.class
)*/
public interface TransactionApi {
    @GetMapping("/balance/{userId}")
    BigDecimal getDepositBalance(@PathVariable Long userId);
}