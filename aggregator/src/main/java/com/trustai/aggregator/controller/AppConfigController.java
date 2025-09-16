package com.trustai.aggregator.controller;

import com.trustai.aggregator.service.AppConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
@Slf4j
public class AppConfigController {
    private final AppConfigService appConfigService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getFrontendConfig() {
        return ResponseEntity.ok(appConfigService.getFrontendConfig());
    }
}
