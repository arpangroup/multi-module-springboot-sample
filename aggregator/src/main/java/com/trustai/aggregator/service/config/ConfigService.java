package com.trustai.aggregator.service.config;

import com.trustai.aggregator.dto.AppConfigUpdateRequest;
import com.trustai.aggregator.dto.ConfigProperty;
import com.trustai.common.exceptions.RestCallException;
import com.trustai.common.lifecycle.ReloadManager;
import com.trustai.income_service.income.service.IncomeDistributionService;
import com.trustai.notification_service.config.MailConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigService {
    private final RestClient restClient;
    private final ContextRefresher contextRefresher;
    private final IncomeDistributionService incomeDistributionService;
    private final ReloadManager reloadManager;
    private final ApplicationContext applicationContext;
    private final MailConfig mailConfig;

    @Value("${config.server.url}")
    private String configServerUrl;

    // simple in-memory cache
    private List<ConfigProperty> cachedConfigs = new ArrayList<>();

    /**
     * Fetch configs from cache if available,
     * otherwise load from server (on first call).
     */
    public List<ConfigProperty> fetchConfig() {
        if (!cachedConfigs.isEmpty()) {
            log.info("📦 Returning cached config: {} entries", cachedConfigs.size());
            return cachedConfigs;
        }
        return loadFromServer();
    }

    /**
     * Reloads spring context + refreshes cache
     */
    public Set<String> reloadConfig() {
        log.info("♻️ Reloading configuration using ContextRefresher");

        try {
            // Returns the keys that got updated
            Set<String> updatedKeys = contextRefresher.refresh();
            log.info("✅ Spring context reloaded. Updated keys: {}", updatedKeys);

            // refresh cache from config server
            loadFromServer();

            // refresh
            reloadManager.reloadAll();
            reloadMailSender();

            return updatedKeys;
        } catch (Exception ex) {
            log.error("❌ Failed to reload config: {}", ex.getMessage(), ex);
            throw new RestCallException("Failed to reload config: " + ex.getMessage(), ex.getCause());
        }

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

    public void reloadMailSender() {
        log.info("reloadMailSender........");
        if (applicationContext instanceof ConfigurableApplicationContext configurableContext) {
            ConfigurableListableBeanFactory beanFactory = configurableContext.getBeanFactory();

            if (beanFactory instanceof DefaultListableBeanFactory defaultFactory) {
                // Destroy old singleton
                if (defaultFactory.containsSingleton("javaMailSender")) {
                    defaultFactory.destroySingleton("javaMailSender");
                }

                // Re-register new instance built from latest properties
                defaultFactory.registerSingleton("javaMailSender", mailConfig.getJavaMailSender());

                log.info("✅ JavaMailSender reloaded with latest mail configuration");
            } else {
                log.warn("⚠️ BeanFactory is not a DefaultListableBeanFactory — cannot reload mail sender");
            }
        } else {
            log.warn("⚠️ ApplicationContext is not configurable — cannot reload mail sender");
        }
    }

    public String addConfig(ConfigProperty request) {
        log.info("🔧 Adding new config to server at {}", configServerUrl);
        log.debug("📝 Config payload: {}", request);

        request.setProfile("default");
        request.setApplication("nft_app");
        try {
            String response = restClient.post()
                    .uri(configServerUrl + "/add")
                    .body(request)
                    .retrieve()
                    .body(String.class);

            log.debug("✅ Successfully added config. Server response: {}", response);
            return response;
        } catch (Exception ex) {
            log.error("❌ Error while adding config to {}: {}", configServerUrl, ex.getMessage(), ex);
            throw new RestCallException("Failed to add config: " +  ex.getMessage(), ex.getCause());
        }
    }

    public String updateConfigs(List<AppConfigUpdateRequest> updates) {
        log.info("🔄 Updating {} config(s) at {}", updates.size(), configServerUrl);
        log.debug("📝 Update payload: {}", updates);

        try {
            String response = restClient.put()
                    .uri(configServerUrl + "/update")
                    .body(updates)
                    .retrieve()
                    .body(String.class);

            log.debug("✅ Successfully updated configs. Server response: {}", response);
            return response;
        } catch (Exception ex) {
            log.error("❌ Error while updating configs: {}", ex.getMessage(), ex);
            throw new RestCallException("Failed to update config: " +  ex.getMessage(), ex.getCause());
        }
    }

    private List<ConfigProperty> loadFromServer() {
        log.info("🔄 Fetching configuration from config server: {}", configServerUrl);

        try {
            List<ConfigProperty> response = restClient.get()
                    .uri(configServerUrl)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ConfigProperty>>() {});

            cachedConfigs = response;
            log.info("✅ Cache updated: {} configs", response.size());
            return cachedConfigs;
        } catch (Exception ex) {
            log.error("❌ Error while fetching config from {}: {}", configServerUrl, ex.getMessage(), ex);
            throw new RestCallException("Failed to fetch config: " +  ex.getMessage(), ex.getCause());
        }
    }

}
