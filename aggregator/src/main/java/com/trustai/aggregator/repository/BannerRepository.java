package com.trustai.aggregator.repository;

import com.trustai.aggregator.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
    boolean existsByTitle(String title);
}
