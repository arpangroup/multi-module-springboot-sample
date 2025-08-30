package com.trustai.common.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        Object errorMessage = request.getSession().getAttribute("error_message");
        if (errorMessage != null) {
            model.addAttribute("error_message", errorMessage.toString());
            request.getSession().removeAttribute("error_message"); // clear after showing
        }
        return "login";
    }
}
