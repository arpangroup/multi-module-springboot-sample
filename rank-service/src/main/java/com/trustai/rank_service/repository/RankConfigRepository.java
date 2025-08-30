package com.trustai.rank_service.repository;

import com.trustai.rank_service.entity.RankConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RankConfigRepository extends JpaRepository<RankConfig, Long> {
    Optional<RankConfig> findByCode(String code);

    // Finds all active rank configs, ordered from highest to lowest rank order
    List<RankConfig> findAllByActiveTrueOrderByRankOrderDesc();
    //List<RankConfig> findAllByActiveTrueOrderByRankOrder();
}
