package com.trustai.income_service.income.service;

import com.trustai.common.api.RankConfigApi;
import com.trustai.common.dto.RankConfigDto;
import com.trustai.common.lifecycle.Reloadable;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class RankConfigApiCache implements Reloadable {
    private final Map<String, RankConfigDto> rankCache = new ConcurrentHashMap<>();
    private final RankConfigApi rankConfigApi;

    @Override
    public void reload() {
        try {
            log.info("Reloading all rank configs into cache...");
            rankCache.clear();
            rankConfigApi.getAllRanks().forEach(cfg -> rankCache.put(cfg.getCode(), cfg));
            log.info("✅ Cached {} rank configs", rankCache.size());
        } catch (Exception e) {
            log.error("❌ Failed to reload rank configs from API", e);
        }
    }

    public RankConfigDto getByRankCode(String rankCode) {
        return rankCache.computeIfAbsent(rankCode, code -> {
            log.info("⚠️ Rank {} not in cache, fetching from API...", code);
            try {
                RankConfigDto dto = rankConfigApi.getRankConfigByRankCode(code);
                if (dto != null) {
                    log.info("✅ Cached rank {}", code);
                } else {
                    log.warn("❌ Rank {} not found in API", code);
                }
                return dto;
            } catch (Exception e) {
                log.error("❌ Failed to fetch rank {} from API", code, e);
                return null; // stays absent
            }
        });
    }

    public Map<String, RankConfigDto> getAllCached() {
        return Map.copyOf(rankCache);
    }

}
