package com.trustai.orderservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/health")
@RestController
public class DemoOrderController {
    @GetMapping("/orders")
    public String demoOrders() {
        return "Running Order Service.....";
    }
}
