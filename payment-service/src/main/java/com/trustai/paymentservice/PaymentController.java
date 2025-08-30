package com.trustai.paymentservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {
    @GetMapping("/payments")
    public String hello() {
        return "Running Payment Service....";
    }
}
