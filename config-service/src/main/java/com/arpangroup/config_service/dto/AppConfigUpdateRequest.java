package com.arpangroup.config_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AppConfigUpdateRequest {
    private String key;
    private String value;
}
