package com.arpangroup.config_service.controller;

import com.arpangroup.config_service.repository.ConfigPropertyRepository;
import com.arpangroup.config_service.service.ConfigService;
import com.arpangroup.config_service.dto.AppConfigUpdateRequest;
import com.arpangroup.config_service.entity.ConfigProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:8888", "http://localhost:3000"})
@RestController
@RequestMapping("/api/v1/configs")
@RequiredArgsConstructor
@Slf4j
public class ConfigAdminController {
    private final ConfigService configService;
    private final ConfigPropertyRepository configRepository;

    @GetMapping("/{app}/{profile}")
    public ResponseEntity<Map<String, String>> getConfig(
            @PathVariable String app,
            @PathVariable String profile) {
        log.info("Received request to get config for app='{}' and profile='{}'", app, profile);
        Map<String, String> props = Map.of(
                "app.name", "MyApp",
                "feature.toggle", "true"
        );
        log.debug("Returning config properties: {}", props);
        return ResponseEntity.ok(props);
    }

    @GetMapping
    public ResponseEntity<List<ConfigProperty>> getALlConfigs() {
        log.info("Fetching all config properties from database");
        return ResponseEntity.ok(configService.getAllConfigs());
    }

    @PostMapping("/reload")
    public ResponseEntity<String> reloadConfig() {
        log.info("Received request to reload configuration");
        configService.loadConfig(); // or force reload
        log.info("Configuration successfully reloaded");
        return ResponseEntity.ok("Config reloaded");
    }

    @PostMapping("/add")
    public ResponseEntity<?> addConfig(@RequestBody ConfigProperty request) {
        log.info("Received request to add new config: {}", request);
        try {
            request.setProfile("default");
            request.setApplication("nft_app");
            configService.addConfig(request);

            log.info("Successfully added new config: {}", request.getKey());
            return ResponseEntity.ok("Config added");
        } catch (Exception e) {
            log.error("Failed to add config: {}. Error: {}", request.getKey(), e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateConfigs(@RequestBody List<AppConfigUpdateRequest> updates) {
        log.info("Received request to update {} config entries", updates.size());
        for (AppConfigUpdateRequest req : updates) {
            log.debug("Updating config key: {}, new value: {}", req.getKey(), req.getValue());
            configRepository.findByKey(req.getKey()).ifPresent(config -> {
                config.setValue(req.getValue());
                configRepository.save(config);
                log.info("Updated config key: {} with new value: {}", req.getKey(), req.getValue());
            });
        }
        log.info("All applicable config entries updated successfully");
        return ResponseEntity.ok("Configs updated");
    }
}
