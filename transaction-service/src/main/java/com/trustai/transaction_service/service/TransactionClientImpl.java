//package com.trustai.transaction_service.service;
//
//import com.trustai.common.client.TransactionClient;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//
//@Service
//@RequiredArgsConstructor
//public class TransactionClientImpl implements TransactionClient {
//    private final DepositService depositService;
//
//    @Override
//    public BigDecimal getDepositBalance(Long userId) {
//        return depositService.getTotalDeposit(userId);
//    }
//}
