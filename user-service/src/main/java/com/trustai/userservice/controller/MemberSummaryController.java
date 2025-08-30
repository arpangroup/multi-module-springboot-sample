package com.trustai.userservice.controller;

import com.trustai.common.controller.BaseController;
import com.trustai.userservice.hierarchy.service.MemberSummaryService;
import com.trustai.userservice.user.dto.MemberSummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/v1/users/metrics")
@RequiredArgsConstructor
@Slf4j
public class MemberSummaryController extends BaseController {
    private final MemberSummaryService memberSummaryService;

    @GetMapping("/member-summary")
    public MemberSummaryResponse getMemberSummary(
          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate start,
          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate  end) {
        Long userId = getCurrentUserId();
        LocalDateTime startDateTime = start != null ? start.atStartOfDay() : null;
        LocalDateTime endDateTime = end != null ? end.atTime(LocalTime.MAX) : null;
        log.info("Received request for member-summary: userId: {}, start = {}, end = {}", userId, startDateTime, endDateTime);

        return memberSummaryService.getMemberSummary(userId, startDateTime, endDateTime);
    }

}
