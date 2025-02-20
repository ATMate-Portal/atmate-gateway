package com.atmate.portal.gateway.atmategateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "A aplicação está a funcionar corretamente! Build Automático da API Gateway!";
    }
}
