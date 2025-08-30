package com.trustai.common.api.impl;

import com.trustai.common.api.WalletApi;
import com.trustai.common.dto.TransactionDto;
import com.trustai.common.dto.WalletUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

import static com.trustai.common.utils.RestCallHandler.handleRestCall;


@Service
//@ConditionalOnProperty(name = "user.api.rest.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class WalletApiRestClientImpl implements WalletApi {
    private final RestClient restClient;

    public WalletApiRestClientImpl(@Qualifier("v1ApiRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public BigDecimal getWalletBalance(Long userId) {
        log.info("Calling getWalletBalance with userId={}", userId);
        return handleRestCall(() ->
            restClient.get()
                .uri("/wallet/balance/{userId}", userId)
                .retrieve()
                .body(BigDecimal.class)
        );
    }

    @Override
    public TransactionDto updateWalletBalance(Long userId, WalletUpdateRequest request) {
        log.info("Calling updateWalletBalance with userId={}, request={}", userId, request);

        return handleRestCall(() ->
            restClient.post()
                .uri("/wallet/update/{userId}", userId)
                .body(request)
                .retrieve()
                .body(TransactionDto.class)
        );
    }
}
