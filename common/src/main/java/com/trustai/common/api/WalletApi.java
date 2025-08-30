package com.trustai.common.api;

import com.trustai.common.dto.TransactionDto;
import com.trustai.common.dto.WalletUpdateRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

/*@FeignClient(
        name = "ServiceName.WalletClient",
        url = CommonConstants.BASE_URL,
        path = CommonConstants.PATH_WALLET_SERVICE,
        configuration = FeignConfig.class
)*/
public interface WalletApi {
    @GetMapping("/wallet/balance/{userId}")
    BigDecimal getWalletBalance(@PathVariable("userId") Long userId);

    @PostMapping("/wallet/update/{userId}")
    TransactionDto updateWalletBalance(
            @PathVariable("userId") Long userId,
            @RequestBody WalletUpdateRequest request
    );

}
