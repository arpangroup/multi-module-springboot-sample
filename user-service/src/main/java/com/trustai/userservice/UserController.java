package com.trustai.userservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RestController
public class UserController {
    @GetMapping("/users")
    public String hello() {
        return "Running User Service....";
    }
}
