package com.trustai.aggregator.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ConfigProperty {
    private String key;
    private String value;
    private String application;
    private String profile;
    private String valueType;
    private String enumValues;
    private String info;
}
