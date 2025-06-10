package com.atmate.portal.gateway.atmategateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/info")
@Tag(name = "Informação da API")
public class InfoController {

    private final HealthEndpoint healthEndpoint;
    private final Instant startTime = Instant.now();

    @Value("${spring.application.name:atmategateway}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    public InfoController(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    @GetMapping
    @Operation(
            summary = "Obter informação da API",
            description = "Endpoint que retorna informação sobre a API"
    )
    public ResponseEntity<Map<String, Object>> getInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("applicationName", appName);
        info.put("version", appVersion);
        info.put("activeProfile", activeProfile);
        info.put("timestamp", DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss z")
                .format(Instant.now().atZone(ZoneId.of("UTC")))); // Using UTC to avoid previous SEALED issue
        return new ResponseEntity<>(info, HttpStatus.OK);
    }

    @GetMapping("/details")
    @Operation(
            summary = "Obter detalhes da informação da API",
            description = "Endpoint que retorna os detalhes da informação sobre a API"
    )
    public ResponseEntity<Map<String, Object>> getDetails() {
        Map<String, Object> details = new HashMap<>();
        details.put("applicationName", appName);
        details.put("javaVersion", System.getProperty("java.version"));
        details.put("osName", System.getProperty("os.name"));
        details.put("osVersion", System.getProperty("os.version"));
        details.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        details.put("totalMemoryMB", Runtime.getRuntime().totalMemory() / (1024 * 1024));
        details.put("freeMemoryMB", Runtime.getRuntime().freeMemory() / (1024 * 1024));
        return new ResponseEntity<>(details, HttpStatus.OK);
    }


    @GetMapping("/status")
    @Operation(
            summary = "Obter status da API",
            description = "Endpoint que retorna o estado da API"
    )
    public ResponseEntity<Map<String, Object>> getStatus() {
        HealthComponent health = healthEndpoint.health();
        Map<String, Object> status = new HashMap<>();
        status.put("status", health.getStatus().getCode());
        status.put("details", health instanceof org.springframework.boot.actuate.health.CompositeHealth
                ? ((org.springframework.boot.actuate.health.CompositeHealth) health).getComponents()
                : new HashMap<>());
        status.put("uptimeSeconds", (Instant.now().toEpochMilli() - startTime.toEpochMilli()) / 1000);
        HttpStatus httpStatus = health.getStatus().equals(Status.UP) ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return new ResponseEntity<>(status, httpStatus);
    }

    @GetMapping("/version")
    @Operation(
            summary = "Obter versão da API",
            description = "Endpoint que retorna a versão da API"
    )
    public ResponseEntity<Map<String, String>> getVersion() {
        Map<String, String> versionInfo = new HashMap<>();
        versionInfo.put("version", appVersion);
        versionInfo.put("buildTime", getBuildTime());
        return new ResponseEntity<>(versionInfo, HttpStatus.OK);
    }

    private String getBuildTime() {
        return "2025-04-20T12:00:00Z";
    }
}