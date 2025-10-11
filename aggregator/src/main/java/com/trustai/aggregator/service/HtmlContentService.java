package com.trustai.aggregator.service;

import com.trustai.aggregator.entity.HtmlContent;
import com.trustai.aggregator.repository.HtmlContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HtmlContentService {
    private final HtmlContentRepository repository;

    public HtmlContent save(HtmlContent content) {
        return repository.save(content);
    }

    public List<HtmlContent> findAll() {
        return repository.findAll();
    }

    public Optional<HtmlContent> findById(Long id) {
        return repository.findById(id);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public HtmlContent update(Long id, HtmlContent updated) {
        return repository.findById(id).map(existing -> {
            existing.setHtml(updated.getHtml());
            existing.setCss(updated.getCss());
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Content not found"));
    }
}
