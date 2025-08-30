//package com.trustai.investment_service.controller;
//
//import com.trustai.investment_service.dto.StakeSubscriptionRequest;
//import com.trustai.investment_service.entity.UserInvestment;
//import com.trustai.investment_service.service.InvestmentService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/v1/stakes")
//@RequiredArgsConstructor
//@Slf4j
//public class StakeController {
//    private final InvestmentService investmentService;
//
//    @PostMapping("/subscribe")
//    public ResponseEntity<UserInvestment> subscribe(@RequestBody StakeSubscriptionRequest request) {
//        return ResponseEntity.ok(investmentService.subscribeToInvestment(request.getUserId(), request.getSchemaId(), request.getAmount()));
//    }
//}
