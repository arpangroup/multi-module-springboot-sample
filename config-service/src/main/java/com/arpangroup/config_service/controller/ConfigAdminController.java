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

@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000"})
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
        // Example: return hardcoded or DB config values
        Map<String, String> props = Map.of(
                "app.name", "MyApp",
                "feature.toggle", "true"
        );
        return ResponseEntity.ok(props);
    }

    @GetMapping
    public ResponseEntity<List<ConfigProperty>> getALlConfigs() {
        return ResponseEntity.ok(configService.getAllConfigs());
    }

    @PostMapping("/reload")
    public ResponseEntity<String> reloadConfig() {
        log.info("reloadConfig.........");
        configService.loadConfig(); // or force reload
        return ResponseEntity.ok("Config reloaded");
    }

    @PostMapping("/add")
    public ResponseEntity<?> addConfig(@RequestBody ConfigProperty request) {
        log.info("reloadConfig.........");
        try {
            request.setProfile("default");
            request.setApplication("nft_app");
            configService.addConfig(request);
            return ResponseEntity.ok("Config added");
        } catch (Exception e) {
            log.error("Exception: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateConfigs(@RequestBody List<AppConfigUpdateRequest> updates) {
        for (AppConfigUpdateRequest req : updates) {
            configRepository.findByKey(req.getKey()).ifPresent(config -> {
                config.setValue(req.getValue());
                configRepository.save(config);
            });
        }
        return ResponseEntity.ok("Configs updated");
    }
}
