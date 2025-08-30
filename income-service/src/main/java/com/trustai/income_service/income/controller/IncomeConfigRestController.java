package com.trustai.income_service.income.controller;

import com.trustai.income_service.income.entity.TeamIncomeConfig;
import com.trustai.income_service.income.service.TeamIncomeConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/income/configs")
@RequiredArgsConstructor
public class IncomeConfigRestController {
    private final TeamIncomeConfigService teamIncomeConfigService;

    @GetMapping
    public List<TeamIncomeConfig> getAllTeamConfigs() {
        return teamIncomeConfigService.getAll();
    }

    @PutMapping
    public ResponseEntity<Void> updateTeamConfigs(@RequestBody List<TeamIncomeConfig> updatedConfigs) {
        teamIncomeConfigService.updateTeamConfigs(updatedConfigs);
        return ResponseEntity.ok().build();
    }
}
