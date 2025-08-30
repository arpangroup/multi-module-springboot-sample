package com.trustai.notification_service.template.repository;

import com.trustai.notification_service.template.entity.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailTemplateRepository extends CodeBasedTemplateRepository<EmailTemplate> {
}
