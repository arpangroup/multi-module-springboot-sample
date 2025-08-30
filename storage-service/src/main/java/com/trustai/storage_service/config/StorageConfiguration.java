package com.trustai.storage_service.config;

import com.trustai.storage_service.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfiguration {
    @Value("${storage.provider}")
    private String provider;

    @Autowired
    private ApplicationContext context;

    @Bean
    public StorageService storageService() {
        return (StorageService) context.getBean(provider + "StorageService");
    }
}
