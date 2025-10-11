package com.trustai.aggregator.controller;

import com.trustai.aggregator.entity.HtmlContent;
import com.trustai.aggregator.service.HtmlContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/app/html")
@RequiredArgsConstructor
public class HtmlContentController {
    private final HtmlContentService htmlContentService;

    @PostMapping
    public ResponseEntity<HtmlContent> create(@RequestBody HtmlContent content) {
        return ResponseEntity.ok(htmlContentService.save(content));
    }

    @GetMapping
    public ResponseEntity<List<HtmlContent>> getAll() {
        return ResponseEntity.ok(htmlContentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HtmlContent> getById(@PathVariable Long id) {
        return htmlContentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<HtmlContent> update(@PathVariable Long id, @RequestBody HtmlContent content) {
        return ResponseEntity.ok(htmlContentService.update(id, content));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        htmlContentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
