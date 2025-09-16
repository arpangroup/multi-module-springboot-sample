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
            // Common
            Map.entry("app.config.url.base", "https://trustai.co.in"),
            Map.entry("app.config.api.version", "/api/v1"),
            Map.entry("app.config.currency.unit", "USDT"), // ["INR", "USDT"]
            Map.entry("app.config.currency.symbol", "$"),  // ["₹", "$"]
            // Deposit
            Map.entry("app.config.deposit.address", "0x5987d451a2d9f7db04d8e539e4d3d6f8aede71bb"),
            Map.entry("app.config.deposit.amount.min", "50"),
            Map.entry("app.config.deposit.warning", "*Only USDT-BEP-20 deposits accepted. Others will be lost."),
            // Withdraw
            Map.entry("app.config.withdraw.address", "0xABCD1234EFGH5678IJKL"),
            Map.entry("app.config.withdraw.amount.min", "50"),
            Map.entry("app.config.withdraw.service.charge", "5"),
            // UI
            Map.entry("app.config.accepted.file.types", "image/png,image/jpeg,image/gif"),
            Map.entry("app.config.header.main.title", "Welcome to TrustAI"),
            Map.entry("app.config.otp.delay.seconds", 30),
            Map.entry("app.config.support.telegram.link", "https://t.me/your_username"),
            Map.entry("app.config.support.whatsapp.link", "https://wa.me/919876543210")
    );

    public Map<String, Object> getFrontendConfig() {
        //log.info("Fetching dynamic config from ConfigService");
        Map<String, String> dynamicConfigMap = fetchDynamicConfig();

        Map<String, Object> mergedConfig = new HashMap<>(DEFAULT_CONFIG);
        //log.debug("Default config loaded with {} entries", mergedConfig.size());

        // Override only keys present in DEFAULT_CONFIG
        dynamicConfigMap.forEach((key, value) -> {
            if (mergedConfig.containsKey(key)) {
                mergedConfig.put(key, value);
            }
        });

        return mergedConfig;
    }

    private Map<String, String> fetchDynamicConfig() {
        return Optional.ofNullable(configService.fetchConfig())
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(ConfigProperty::getKey, ConfigProperty::getValue, (a, b) -> b));
    }
}
