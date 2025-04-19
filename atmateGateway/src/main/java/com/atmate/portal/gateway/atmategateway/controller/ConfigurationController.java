package com.atmate.portal.gateway.atmategateway.controller;

import com.atmate.portal.gateway.atmategateway.database.dto.ParamsDTO;
import com.atmate.portal.gateway.atmategateway.database.entitites.Configuration;
import com.atmate.portal.gateway.atmategateway.database.services.ConfigurationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Validated
@RestController
@RequestMapping("/config")
public class ConfigurationController {

    @Value("${warning.days.value:warning_days}")
    private String warningDaysVarName;

    @Value("${urgent.days.value:urgent_days}")
    private String urgentDaysVarName;

    @Autowired
    private ConfigurationService configurationService;

    @PostMapping("/setParams")
    public ResponseEntity<String> setParams(@Valid @RequestBody ParamsDTO paramsDTO) {
        log.info("Received request to set parameters: warningDays={}, urgentDays={}",
                paramsDTO.getWarningDays(), paramsDTO.getUrgentDays());

        try {
            Integer warningDays = null;
            Integer urgentDays = null;
            List<Configuration> configsToUpdate = new ArrayList<>();

            // Parse and validate warningDays if provided
            if (paramsDTO.getWarningDays() != null && !paramsDTO.getWarningDays().trim().isEmpty()) {
                try {
                    warningDays = Integer.parseInt(paramsDTO.getWarningDays());
                    if (warningDays <= 0) {
                        log.warn("Invalid warningDays: {} must be positive", warningDays);
                        return ResponseEntity.badRequest().body("Warning days must be positive");
                    }
                } catch (NumberFormatException e) {
                    log.warn("Invalid warningDays format: {}", paramsDTO.getWarningDays());
                    return ResponseEntity.badRequest().body("Warning days must be a valid number");
                }
            }

            // Parse and validate urgentDays if provided
            if (paramsDTO.getUrgentDays() != null && !paramsDTO.getUrgentDays().trim().isEmpty()) {
                try {
                    urgentDays = Integer.parseInt(paramsDTO.getUrgentDays());
                    if (urgentDays <= 0) {
                        log.warn("Invalid urgentDays: {} must be positive", urgentDays);
                        return ResponseEntity.badRequest().body("Urgent days must be positive");
                    }
                } catch (NumberFormatException e) {
                    log.warn("Invalid urgentDays format: {}", paramsDTO.getUrgentDays());
                    return ResponseEntity.badRequest().body("Urgent days must be a valid number");
                }
            }

            // Enforce urgentDays <= warningDays when both are provided or when one depends on the other
            if (warningDays != null || urgentDays != null) {
                // Fetch current configurations if needed
                Optional<Configuration> currentWarningConfig = configurationService.getConfigurationString(warningDaysVarName);
                Optional<Configuration> currentUrgentConfig = configurationService.getConfigurationString(urgentDaysVarName);

                int currentWarningDays = currentWarningConfig.map(config -> Integer.parseInt(config.getVarvalue())).orElse(7);
                int currentUrgentDays = currentUrgentConfig.map(config -> Integer.parseInt(config.getVarvalue())).orElse(2);

                int effectiveWarningDays = warningDays != null ? warningDays : currentWarningDays;
                int effectiveUrgentDays = urgentDays != null ? urgentDays : currentUrgentDays;

                if (effectiveUrgentDays > effectiveWarningDays) {
                    log.warn("Invalid input: urgentDays={} must be less than or equal to warningDays={}",
                            effectiveUrgentDays, effectiveWarningDays);
                    return ResponseEntity.badRequest().body("Urgent days must be less than or equal to warning days");
                }

                // Prepare configurations to update
                if (warningDays != null) {
                    Configuration warningConfig = new Configuration();
                    warningConfig.setVarname(warningDaysVarName);
                    warningConfig.setVarvalue(String.valueOf(warningDays));
                    warningConfig.setDescription("Number of days for warning notification");
                    warningConfig.setIsActive(true);
                    configsToUpdate.add(warningConfig);
                }

                if (urgentDays != null) {
                    Configuration urgentConfig = new Configuration();
                    urgentConfig.setVarname(urgentDaysVarName);
                    urgentConfig.setVarvalue(String.valueOf(urgentDays));
                    urgentConfig.setDescription("Number of days for urgent notification");
                    urgentConfig.setIsActive(true);
                    configsToUpdate.add(urgentConfig);
                }
            }

            // Save configurations if there are any to update
            if (!configsToUpdate.isEmpty()) {
                configurationService.saveConfigurations(configsToUpdate);
                log.info("Successfully updated configurations: warningDays={}, urgentDays={}",
                        warningDays != null ? warningDays : "unchanged",
                        urgentDays != null ? urgentDays : "unchanged");
            } else {
                log.info("No parameters provided to update");
                return ResponseEntity.ok("No parameters updated");
            }

            return ResponseEntity.ok("Parameters updated successfully");
        } catch (Exception e) {
            log.error("Failed to update parameters: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update parameters: " + e.getMessage());
        }
    }

    // Existing /getParams endpoint (for reference)
    @GetMapping("/getParams")
    public ResponseEntity<ParamsDTO> getParams() {
        log.info("Fetching configuration parameters for warning_days_varname={} and urgent_days_varname={}",
                warningDaysVarName, urgentDaysVarName);
        ParamsDTO params = new ParamsDTO();

        configurationService.getConfigurationString(warningDaysVarName)
                .ifPresent(config -> {
                    log.debug("Found {}: {}", warningDaysVarName, config.getVarvalue());
                    params.setWarningDays(config.getVarvalue());
                });
        configurationService.getConfigurationString(urgentDaysVarName)
                .ifPresent(config -> {
                    log.debug("Found {}: {}", urgentDaysVarName, config.getVarvalue());
                    params.setUrgentDays(config.getVarvalue());
                });

        if (params.getWarningDays() == null) {
            log.warn("No configuration found for {}", warningDaysVarName);
            params.setWarningDays("7"); // Default value
        }
        if (params.getUrgentDays() == null) {
            log.warn("No configuration found for {}", urgentDaysVarName);
            params.setUrgentDays("2"); // Default value
        }

        log.info("Returning ParamsDTO: warningDays={}, urgentDays={}",
                params.getWarningDays(), params.getUrgentDays());
        return ResponseEntity.ok(params);
    }
}