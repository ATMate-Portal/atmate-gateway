package com.atmate.portal.gateway.atmategateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/info")
public class InfoController {

        @GetMapping("/details") // This will be /info/details
        public String getDetails() {
            return "This is the details endpoint.";
        }

        @GetMapping("/status") // This will be /info/status
        public String getStatus() {
            return "Application is running.";
        }

        @GetMapping("/version") // This will be /info/version
        public String getVersion() {
            return "Version 1.0";
        }

        @GetMapping() //This will be /info
        public String getInfo(){
            return "General info";
        }
}
