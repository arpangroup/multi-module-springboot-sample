package com.trustai.orderservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RestController
public class OrderController {
    @GetMapping("/orders")
    public String hello() {
        return "Running Order Service.....";
    }
}
