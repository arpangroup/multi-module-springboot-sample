package com.trustai.aggregator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigProperty {
    private String key;
    private String value;
    private String application;
    private String profile;
    private String valueType;
    private String enumValues;
    private String info;

    public ConfigProperty(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
