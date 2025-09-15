package com.trustai.aggregator.controller;

import com.trustai.aggregator.dto.AppConfigUpdateRequest;
import com.trustai.aggregator.dto.ConfigProperty;
import com.trustai.common.dto.ApiResponse;
import com.trustai.common.exceptions.RestCallException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/configs")
@RequiredArgsConstructor
@Slf4j
public class ConfigForwardingController {
    private final RestClient restClient;
    private final ContextRefresher contextRefresher;

    @Value("${config.server.url}")
    private String configServerUrl;

    @GetMapping
    public ResponseEntity<Object> fetchConfig() {
        log.info("📥 Received request to fetch configuration from config server: {}", configServerUrl);
        try {
            Object response = restClient.get()
                    .uri(configServerUrl)
                    .retrieve()
                    .body(Object.class);
            log.debug("✅ Successfully fetched config: {}", response);

            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            log.error("❌ Error while fetching config from {}: {}", configServerUrl, ex.getMessage(), ex);
            throw new RestCallException("Failed to fetch config: " +  ex.getMessage(), ex.getCause());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addConfig(@RequestBody ConfigProperty request) {
        log.info("📥 Received request to add config: {}", request);
        request.setProfile("default");
        request.setApplication("nft_app");
        try {
            String response = restClient.post()
                    .uri(configServerUrl + "/add")
                    .body(request)
                    .retrieve()
                    .body(String.class);

            log.debug("✅ Successfully added config. Server response: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            log.error("❌ Error while adding config to {}: {}", configServerUrl, ex.getMessage(), ex);
            throw new RestCallException("Failed to add config: " +  ex.getMessage(), ex.getCause());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateConfigs(@RequestBody List<AppConfigUpdateRequest> updates) {
        log.info("📥 Received request to update configs. Count: {}", updates.size());
        log.debug("📝 Update payload: {}", updates);

        try {
            String response = restClient.put()
                    .uri(configServerUrl + "/update")
                    .body(updates)
                    .retrieve()
                    .body(String.class);

            log.debug("✅ Successfully updated configs. Server response: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            log.error("❌ Error while updating configs: {}", ex.getMessage(), ex);
            throw new RestCallException("Failed to update config: " +  ex.getMessage(), ex.getCause());
        }
    }

    @PostMapping("/reload")
    public ResponseEntity<Set<String>> reloadConfig() {
        log.info("📥 Received request to reload config from config server: {}", configServerUrl);


        // Returns the keys that got updated
        return ResponseEntity.ok(contextRefresher.refresh());

        /*try {
            String response = restClient.post()
                    .uri(configServerUrl + "/reload")
                    .retrieve()
                    .body(String.class);

            log.debug("✅ Successfully reloaded config. Server response: {}", response);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception ex) {
            log.error("❌ Error while reloading config: {}", ex.getMessage(), ex);
            throw new RestCallException("Failed to reload config: " +  ex.getMessage(), ex.getCause());
        }*/

    }
}
