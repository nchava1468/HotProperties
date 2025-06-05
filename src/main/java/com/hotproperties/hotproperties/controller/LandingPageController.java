package com.hotproperties.hotproperties.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LandingPageController {

    @GetMapping("/")
    public String home() {
        return "index"; // maps to index.html
    }
    @GetMapping("/login")
    public String login() {
        return "login"; // refers to login.html in templates
    }

}
