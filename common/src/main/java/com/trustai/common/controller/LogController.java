package com.trustai.common.controller;


import com.trustai.common.domain.log.PrintLog;
import com.trustai.common.repository.log.PrintLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
public class LogController {
    private final PrintLogRepository printLogRepository;


    @GetMapping
    public Page<PrintLog> getLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        if (userId != null) {
            return printLogRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        } else {
            return printLogRepository.findAll(pageable);
        }
    }
}
