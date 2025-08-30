package com.trustai.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

@Configuration
@Slf4j
public class RestClientConfig {

    @Value("${security.auth.internal-token}")
    private String internalToken;

    private ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {
            log.info("RestClient - Request URI: {} {}", request.getMethod(), request.getURI());
            request.getHeaders().forEach((k, v) -> log.info("Header '{}': {}", k, v));
            if (body != null && body.length > 0) {
                log.debug("Request body: {}", new String(body));
            }
            return execution.execute(request, body);
        };
    }

    private ClientHttpRequestInterceptor loggingResponseInterceptor() {
        return (request, body, execution) -> {
            var response = execution.execute(request, body);
            log.info("Response status: {}", response.getStatusCode());
            response.getHeaders().forEach((k, v) -> log.info("Resp header '{}': {}", k, v));
            return response;
        };
    }

    @Bean
    public RestClient userServiceRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:8080/api/v1/provider")
//                .requestInterceptor((request, body, execution) -> {
//                    log.info("Outgoing request: {} {}", request.getMethod(), request.getURI());
//                    return execution.execute(request, body);
//                })
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + internalToken)
                .requestInterceptor(loggingInterceptor())
                //.requestInterceptor(loggingResponseInterceptor())
                .build();
    }

    @Bean
    public RestClient v1ApiRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:8080/api/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + internalToken)
                .requestInterceptor(loggingInterceptor())
                //.requestInterceptor(loggingResponseInterceptor())
                .build();
    }
}
