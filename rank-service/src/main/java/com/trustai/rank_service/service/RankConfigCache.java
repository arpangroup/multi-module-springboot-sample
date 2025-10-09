package com.trustai.rank_service.service;

import com.trustai.common.lifecycle.Reloadable;
import com.trustai.rank_service.entity.RankConfig;
import com.trustai.rank_service.repository.RankConfigRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RankConfigCache implements Reloadable {
    private final RankConfigRepository rankRepo;
    private final Map<String, RankConfig> rankCache = new HashMap<>();
    private volatile List<RankConfig> orderedRanks = List.of();

    @Override
    public void reload() {
        preload(); // reload from DB
    }

    /**
     * This preload() call happens <b>right after the bean is initialized but the Spring application context is not fully ready yet,</b>
     * so if restClient tries to hit a remote API during this phase,
     * and some properties or beans are not ready yet, it can cause a failure.
     */
    @PostConstruct
    public void preload() {
        log.info("Preloading rank configs into cache...");
        List<RankConfig> ranks = rankRepo.findAllByActiveTrueOrderByRankOrderDesc();
        this.orderedRanks = ranks;
        this.rankCache.clear();
        ranks.forEach(r -> rankCache.put(r.getCode(), r));
        log.info("✅ Cached {} rank configs", rankCache.size());
    }

    public List<RankConfig> getAllRanksOrdered() {
        return orderedRanks;
    }

    public RankConfig getByRankCode(String code) {
        return rankCache.get(code);
    }

}
