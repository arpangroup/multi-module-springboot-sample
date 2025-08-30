package com.trustai.notification_service.template.repository;

import com.trustai.notification_service.template.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

/**
 * This tells Spring that any repository extending this interface must implement findByCode.
 * @param <T>
 */
@NoRepositoryBean
public interface CodeBasedTemplateRepository<T extends Template> extends JpaRepository<T, Long> {
    Optional<T> findByCode(String code);
}