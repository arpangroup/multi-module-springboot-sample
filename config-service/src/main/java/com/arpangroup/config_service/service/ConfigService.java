package com.arpangroup.config_service.service;

import com.arpangroup.config_service.entity.ConfigProperty;
import com.arpangroup.config_service.repository.ConfigPropertyRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ConfigService {
    private final ConfigPropertyRepository repository;
    private final Map<String, String> configMap = new ConcurrentHashMap<>();
    private boolean loaded = false;

    public ConfigService(ConfigPropertyRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void loadConfig() {
        if (!loaded) {
            try {
                repository.findAll().forEach(config -> {
                    configMap.put(config.getKey(), config.getValue());
                    log.debug("Loaded config: {} = {}", config.getKey(), config.getValue());
                });
                loaded = true;
                log.info("Configuration loaded successfully.");
            } catch (Exception e) {
                log.error("Error while loading configuration: {}", e.getMessage(), e);
            }
        } else {
            log.info("Configuration already loaded. Skipping reload.");
        }
    }

    public ConfigProperty addConfig(ConfigProperty request) throws Exception {
        log.info("Adding new config with key: {}", request.getKey());

        repository.findByKey(request.getKey())
                .ifPresent(cfg -> {
                    log.warn("Config with key '{}' already exists", request.getKey());
                    throw new RuntimeException("Key already exists");
                });

        ConfigProperty config = new ConfigProperty(request.getKey(), request.getValue(), request.getEnumValues());
        config.setApplication(request.getApplication());
        config.setProfile(request.getProfile());
        config.setLabel(request.getLabel());
        config.setInfo(request.getInfo());
        config = repository.save(config);
        configMap.put(config.getKey(), config.getValue());

        log.info("Successfully added config: {} = {}", config.getKey(), config.getValue());
        return config;
    }

    public List<ConfigProperty> getAllConfigs() {
        log.info("Fetching all config properties from database");
        return repository.findAll();
    }

    public String get(String key) {
        log.debug("Fetching config value for key: {}", key);
        return configMap.get(key);
    }

    public Optional<String> getOptional(String key) {
        log.debug("Fetching optional config value for key: {}", key);
        return Optional.ofNullable(configMap.get(key));
    }

    // auto-convert values to int
    public int getInt(String key, int defaultValue) {
        String value = configMap.get(key);
        try {
            int intValue = value != null ? Integer.parseInt(value) : defaultValue;
            log.debug("Parsed int config: {} = {}", key, intValue);
            return intValue;
        } catch (NumberFormatException e) {
            log.warn("Invalid int format for key '{}': '{}'. Using default: {}", key, value, defaultValue);
            return defaultValue;
        }
    }

    // auto-convert values to boolean
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = configMap.get(key);
        boolean result = value != null ? Boolean.parseBoolean(value) : defaultValue;
        log.debug("Parsed boolean config: {} = {}", key, result);
        return result;
    }

    // auto-convert values to double
    public double getDouble(String key, double defaultValue) {
        String value = configMap.get(key);
        try {
            double doubleValue = value != null ? Double.parseDouble(value) : defaultValue;
            log.debug("Parsed double config: {} = {}", key, doubleValue);
            return doubleValue;
        } catch (NumberFormatException e) {
            log.warn("Invalid double format for key '{}': '{}'. Using default: {}", key, value, defaultValue);
            return defaultValue;
        }
    }
}
