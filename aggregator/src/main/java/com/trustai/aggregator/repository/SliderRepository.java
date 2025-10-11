package com.trustai.aggregator.repository;

import com.trustai.aggregator.entity.Slider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SliderRepository extends JpaRepository<Slider, Long> {
    boolean existsByName(String name);
}