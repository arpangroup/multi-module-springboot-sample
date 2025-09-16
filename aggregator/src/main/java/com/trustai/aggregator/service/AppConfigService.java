package com.trustai.aggregator.service;

import com.trustai.aggregator.dto.ConfigProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppConfigService {
    private final ConfigService configService;

    // Define all defaults in one place
    private static final Map<String, Object> DEFAULT_CONFIG = Map.ofEntries(
            Map.entry("BASE_URL", "http://trustai.co.in"),
            Map.entry("API_VERSION", "/api/v1"),
            Map.entry("CURRENCY_UNIT", "USDT"),
            Map.entry("CURRENCY_UNIT_DEFAULT", "INR"),
            Map.entry("CURRENCY_SYMBOL", "$"),
            Map.entry("CURRENCY_SYMBOL_DEFAULT", "₹"),
            Map.entry("DEPOSIT_ADDRESS", "0x5987d451a2d9f7db04d8e539e4d3d6f8aede71bb"),
            Map.entry("WITHDRAW_ADDRESS", "0xABCD1234EFGH5678IJKL"),
            Map.entry("MINIMUM_WITHDRAW", "50"),
            Map.entry("SERVICE_CHARGE", "5"),
            Map.entry("ACCEPTED_FILE_TYPES", "image/png,image/jpeg,image/gif"),
            Map.entry("MAIN_HEADER_TITLE", "Welcome to TrustAI")
    );

    // Dynamic overrides mapping: backend config key -> frontend key
    private static final Map<String, String> OVERRIDE_MAPPING = Map.of(
            "app.service.charge", "SERVICE_CHARGE",
            "app.currency.unit", "CURRENCY_UNIT",
            "app.currency.symbol", "CURRENCY_SYMBOL",
            "app.header.main.title", "MAIN_HEADER_TITLE"
            // add more mappings here...
    );

    public Map<String, Object> getFrontendConfig() {
        //log.info("Fetching dynamic config from ConfigService");
        Map<String, String> dynamicConfigMap = fetchDynamicConfig();

        Map<String, Object> mergedConfig = new HashMap<>(DEFAULT_CONFIG);
        //log.debug("Default config loaded with {} entries", mergedConfig.size());

        // Apply all overrides in one go
        OVERRIDE_MAPPING.forEach((dynamicKey, frontendKey) ->
                overrideIfPresent(mergedConfig, dynamicConfigMap, dynamicKey, frontendKey));

        //log.info("Merged config ready with {} entries", mergedConfig.size());
        return mergedConfig;
    }

    private Map<String, String> fetchDynamicConfig() {
        return Optional.ofNullable(configService.fetchConfig())
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(ConfigProperty::getKey, ConfigProperty::getValue, (a, b) -> b));
    }

    private void overrideIfPresent(Map<String, Object> target,
                                   Map<String, String> dynamicConfig,
                                   String dynamicKey,
                                   String frontendKey) {
        Optional.ofNullable(dynamicConfig.get(dynamicKey))
                .ifPresent(value -> target.put(frontendKey, value));
    }
}
