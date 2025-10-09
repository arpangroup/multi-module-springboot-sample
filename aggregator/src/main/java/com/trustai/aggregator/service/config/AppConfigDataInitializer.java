package com.trustai.aggregator.service.config;

import com.trustai.aggregator.dto.ConfigProperty;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppConfigDataInitializer {

    private final ConfigService configService;

    @PostConstruct
    public void init() {
        log.info("🚀 Initializing default app config into config server...");

        try {
            // Fetch existing configs from server
            List<ConfigProperty> existingConfigs = configService.fetchConfig();
            Set<String> existingKeys = existingConfigs.stream()
                    .map(ConfigProperty::getKey)
                    .collect(Collectors.toSet());

            // Find which defaults are missing
            Map<String, Object> defaults = AppConfigService.getDefaultConfig();
            List<ConfigProperty> toInsert = defaults.entrySet().stream()
                    .filter(entry -> !existingKeys.contains(entry.getKey()))
                    .map(entry -> new ConfigProperty(entry.getKey(), String.valueOf(entry.getValue())))
                    .toList();

            if (!toInsert.isEmpty()) {
                log.info("📝 Adding {} missing config(s): {}", toInsert.size(),
                        toInsert.stream().map(ConfigProperty::getKey).toList());

                toInsert.forEach(configService::addConfig);
            } else {
                log.info("✅ All default configs already exist.");
            }

        } catch (Exception ex) {
            log.error("❌ Failed to initialize default configs: {}", ex.getMessage(), ex);
        }
    }
}
