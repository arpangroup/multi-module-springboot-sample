package com.arpangroup.config_service.controller;

import com.arpangroup.config_service.repository.ConfigPropertyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

//@RestController
//public class ConfigController {
//    private final ConfigPropertyRepository repository
//
//    @GetMapping("/{app}/{profile}")
//    public ResponseEntity<Map<String, String>> getConfig(@PathVariable String app, @PathVariable String profile) {
//        Map<String, String> props = Map.of(
//                "app.name", "MyApp",
//                "feature.toggle", "true"
//        );
//        return ResponseEntity.ok(props);
//    }
//}
