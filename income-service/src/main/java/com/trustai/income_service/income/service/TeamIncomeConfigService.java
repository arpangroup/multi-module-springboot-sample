package com.trustai.income_service.income.service;

import com.trustai.income_service.income.entity.TeamIncomeConfig;
import com.trustai.income_service.income.entity.TeamIncomeKey;
import com.trustai.income_service.income.repository.TeamIncomeConfigRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamIncomeConfigService {
    private final TeamIncomeConfigRepository teamIncomeConfigRepository;


    public List<TeamIncomeConfig> getAll() {
        return new ArrayList<>(teamIncomeConfigRepository.findAll());
    }


    /*@Transactional
    public void updateTeamConfigs(List<TeamIncomeConfig> updatedConfigs) {
        for (TeamIncomeConfig config : updatedConfigs) {
            Optional<TeamIncomeConfig> existingOpt = teamIncomeConfigRepository.findById(config.getRankCode());
            if (existingOpt.isPresent()) {
                TeamIncomeConfig existing = existingOpt.get();
                existing.setIncomePercentages(config.getIncomePercentages());
                teamIncomeConfigRepository.save(existing);
            } else {
                teamIncomeConfigRepository.save(config); // in case it's new
            }
        }
    }*/

    @Transactional
    public void updateTeamConfigs(List<TeamIncomeConfig> updatedConfigs) {
        for (TeamIncomeConfig config : updatedConfigs) {
            TeamIncomeKey id = config.getId(); // contains rankCode + level
            Optional<TeamIncomeConfig> existingOpt = teamIncomeConfigRepository.findById(id);

            if (existingOpt.isPresent()) {
                TeamIncomeConfig existing = existingOpt.get();
                existing.setPayoutPercentage(config.getPayoutPercentage());
                teamIncomeConfigRepository.save(existing);
            } else {
                teamIncomeConfigRepository.save(config); // insert new row if missing
            }
        }
    }

}
