package com.trustai.aggregator.repository;

import com.trustai.aggregator.entity.HtmlContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HtmlContentRepository extends JpaRepository<HtmlContent, Long> {
}
