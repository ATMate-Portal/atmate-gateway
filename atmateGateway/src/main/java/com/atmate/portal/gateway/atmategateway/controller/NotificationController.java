package com.atmate.portal.gateway.atmategateway.controller;

import com.atmate.portal.gateway.atmategateway.database.entitites.ClientNotificationConfig;
import com.atmate.portal.gateway.atmategateway.database.services.ClientNotificationConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    ClientNotificationConfigService clientNotificationConfigService;

    @GetMapping("/getNotificationConfig")
    public ResponseEntity<List<ClientNotificationConfig>> getNotificationConfigs() {
        try {
            log.info("Fetching notification configurations");
            List<ClientNotificationConfig> configs = clientNotificationConfigService.getAllClientNotificationConfigs();
            if (configs.isEmpty()) {
                log.info("No notification configurations found.");
                return ResponseEntity.ok().body(configs); // Return empty list with 200 OK
            }
            log.info("Successfully retrieved {} notification configurations.", configs.size());
            return ResponseEntity.ok().body(configs);
        } catch (Exception e) {
            log.error("Error fetching notification configurations: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null); // Return 500 Internal Server Error
        }
    }

}
