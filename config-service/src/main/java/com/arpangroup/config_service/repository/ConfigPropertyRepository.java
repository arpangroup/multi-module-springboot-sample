package com.arpangroup.config_service.repository;

import com.arpangroup.config_service.entity.ConfigProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigPropertyRepository extends JpaRepository<ConfigProperty, Long> {
    List<ConfigProperty> findByApplicationAndProfile(String application, String profile);
    Optional<ConfigProperty> findByKey(String key);
}
