package com.trustai.common.api.impl;

import com.trustai.common.api.IncomeApi;
import com.trustai.common.constants.CommonConstants;
import com.trustai.common.dto.IncomeSummaryDto;
import com.trustai.common.dto.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.trustai.common.utils.RestCallHandler.handleRestCall;

@Service
@Slf4j
public class IncomeApiRestClientImpl implements IncomeApi {
    private final RestClient restClient;

    public IncomeApiRestClientImpl(@Qualifier("v1ApiRestClient") RestClient restClient) {
        this.restClient = restClient;
    }


    @Override
    public List<IncomeSummaryDto> getIncomeSummary(Long userId) {
        log.info("Calling getIncomeSummary with userId={}", userId);
        return handleRestCall(() -> {
            IncomeSummaryDto[] incomes = restClient.get()
                    .uri("/incomes/summary")
                    .headers(httpHeaders -> httpHeaders.add(CommonConstants.HEADER_ACTING_USER_ID, userId.toString()))
                    .retrieve()
                    //.body(new ParameterizedTypeReference<List<UserInfo>>() {});
                    .body(IncomeSummaryDto[].class);
            return Arrays.asList(incomes);
        });
    }

    @Override
    public Map<Long, BigDecimal> getUserShares(List<Long> userIds, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Calling getUserShares with userIds: {}, startDate: {}, endDate: {}", userIds, startDate, endDate);
        return handleRestCall(() -> {
            return restClient.post()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/incomes/user-shares");
                        if (startDate != null) {
                            builder.queryParam("startDate", startDate.toString());
                        }
                        if (endDate != null) {
                            builder.queryParam("endDate", endDate.toString());
                        }
                        return builder.build();
                    })
                    .body(userIds)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<Long, BigDecimal>>() {});
        });
    }
}

/*
return handleRestCall(() -> {
            UserInfo[] response = restClient.post()
                    .uri("/users/by-ids")
                    .body(userIds)
                    .retrieve()
                    .body(UserInfo[].class);
                    return Arrays.asList(response);
        });
 */