package com.trustai.common.api.impl;

import com.trustai.common.api.TransactionApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

import static com.trustai.common.utils.RestCallHandler.handleRestCall;


@Service
@Slf4j
public class TransactionApiRestClientImpl implements TransactionApi {
    private final RestClient restClient;

    public TransactionApiRestClientImpl(@Qualifier("v1ApiRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public BigDecimal getDepositBalance(Long userId) {
        log.info("Calling getDepositBalance with userId={}", userId);

        return handleRestCall(() ->
            restClient.get()
                .uri("/{userId}", userId)
                .retrieve()
                .body(BigDecimal.class)
        );

    }
}
