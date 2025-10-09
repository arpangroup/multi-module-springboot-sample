package com.trustai.aggregator.controller.config;

import com.trustai.aggregator.dto.AppConfigUpdateRequest;
import com.trustai.aggregator.dto.ConfigProperty;
import com.trustai.aggregator.service.config.ConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/configs")
@RequiredArgsConstructor
@Slf4j
public class ConfigForwardingController {
    private final ConfigService configService;

    @GetMapping
    public ResponseEntity<Object> fetchConfig() {
        log.info("[GET] /configs - Fetch configuration request received");
        return ResponseEntity.ok(configService.fetchConfig());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addConfig(@RequestBody ConfigProperty request) {
        log.info("[POST] /configs/add - Add config request received");
        log.debug("Config to add: {}", request);
        return ResponseEntity.ok(configService.addConfig(request));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateConfigs(@RequestBody List<AppConfigUpdateRequest> updates) {
        log.info("[PUT] /configs/update - Update config request received. Count: {}", updates.size());
        log.debug("Update payload: {}", updates);
        return ResponseEntity.ok(configService.updateConfigs(updates));
    }

    @PostMapping("/reload")
    public ResponseEntity<Set<String>> reloadConfig() {
        log.info("[POST] /configs/reload - Reload config request received");
        return ResponseEntity.ok(configService.reloadConfig());

    }
}
