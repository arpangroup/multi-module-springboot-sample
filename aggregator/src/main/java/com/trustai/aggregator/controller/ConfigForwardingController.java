package com.trustai.aggregator.controller;

import com.trustai.aggregator.dto.AppConfigUpdateRequest;
import com.trustai.aggregator.dto.ConfigProperty;
import com.trustai.common.dto.ApiResponse;
import com.trustai.common.exceptions.RestCallException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;

@RestController
@RequestMapping("/api/v1/configs")
@RequiredArgsConstructor
@Slf4j
public class ConfigForwardingController {
    private final RestClient restClient;

    @Value("${config.server.url}")
    private String configServerUrl;

    @GetMapping
    public ResponseEntity<Object> fetchConfig() {
        try {
            Object response = restClient.get()
                    .uri(configServerUrl)
                    .retrieve()
                    .body(Object.class);
            System.out.println("Config server raw response: " + response);

            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            log.error("Exception in fetchConfig: " + ex.getMessage());
            throw new RestCallException("Failed to fetch config: " +  ex.getMessage(), ex.getCause());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addConfig(@RequestBody ConfigProperty request) {
        log.info("addConfig.........");
        request.setProfile("default");
        request.setApplication("nft_app");
        try {
            String response = restClient.post()
                    .uri(configServerUrl + "/add")
                    .body(request)
                    .retrieve()
                    .body(String.class);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            log.error("Exception in addConfig: " + ex.getMessage());
            throw new RestCallException("Failed to add config: " +  ex.getMessage(), ex.getCause());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateConfigs(@RequestBody List<AppConfigUpdateRequest> updates) {
        try {
            String response = restClient.put()
                    .uri(configServerUrl + "/update")
                    .body(updates)
                    .retrieve()
                    .body(String.class);

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            log.error("Exception in updateConfigs: " + ex.getMessage());
            throw new RestCallException("Failed to update config: " +  ex.getMessage(), ex.getCause());
        }
    }

    @PostMapping("/reload")
    public ResponseEntity<ApiResponse<?>> reloadConfig() {
        log.info("reloadConfig.........");
        try {
            String response = restClient.post()
                    .uri(configServerUrl + "/reload")
                    .retrieve()
                    .body(String.class);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception ex) {
            log.error("Exception in reloadConfig: " + ex.getMessage());
            throw new RestCallException("Failed to reload config: " +  ex.getMessage(), ex.getCause());
        }

    }
}
