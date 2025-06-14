package com.atmate.portal.gateway.atmategateway.controller;

import com.atmate.portal.gateway.atmategateway.dto.OperationHistoryRequest;
import com.atmate.portal.gateway.atmategateway.dto.ParamsResponse;
import com.atmate.portal.gateway.atmategateway.database.entitites.Configuration;
import com.atmate.portal.gateway.atmategateway.database.services.ConfigurationService;
import com.atmate.portal.gateway.atmategateway.database.services.OperationHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/config")
@Tag(name = "Configurações")
public class ConfigurationController {

    @Value("${warning.days.value:warning_days}")
    private String warningDaysVarName;

    @Value("${urgent.days.value:urgent_days}")
    private String urgentDaysVarName;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    OperationHistoryService operationHistoryService;

    @PostMapping("/setParams")
    @Operation(
            summary = "Atualizar parâmetros de urgência",
            description = "Endpoint que atualiza os dias de aviso/urgência da página principal"
    )
    public ResponseEntity<String> setParams(@RequestBody ParamsResponse paramsResponse) {
        log.info("Received request to set parameters: warningDays={}, urgentDays={}",
                paramsResponse.getWarningDays(), paramsResponse.getUrgencyDays());

        try {
            Integer warningDays = null;
            Integer urgentDays = null;
            List<Configuration> configsToUpdate = new ArrayList<>();

            if (paramsResponse.getWarningDays() != null && !paramsResponse.getWarningDays().trim().isEmpty()) {
                try {
                    warningDays = Integer.parseInt(paramsResponse.getWarningDays());
                    if (warningDays <= 0) {
                        log.warn("Invalid warningDays: {} must be positive", warningDays);
                        return ResponseEntity.badRequest().body("Warning days must be positive");
                    }
                } catch (NumberFormatException e) {
                    log.warn("Invalid warningDays format: {}", paramsResponse.getWarningDays());
                    return ResponseEntity.badRequest().body("Warning days must be a valid number");
                }
            }

            if (paramsResponse.getUrgencyDays() != null && !paramsResponse.getUrgencyDays().trim().isEmpty()) {
                try {
                    urgentDays = Integer.parseInt(paramsResponse.getUrgencyDays());
                    if (urgentDays <= 0) {
                        log.warn("Invalid urgentDays: {} must be positive", urgentDays);
                        return ResponseEntity.badRequest().body("Urgent days must be positive");
                    }
                } catch (NumberFormatException e) {
                    log.warn("Invalid urgentDays format: {}", paramsResponse.getUrgencyDays());
                    return ResponseEntity.badRequest().body("Urgent days must be a valid number");
                }
            }

            if (warningDays != null || urgentDays != null) {
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

            if (!configsToUpdate.isEmpty()) {
                configurationService.saveConfigurations(configsToUpdate);
                log.info("Successfully updated configurations: warningDays={}, urgentDays={}",
                        warningDays != null ? warningDays : "unchanged",
                        urgentDays != null ? urgentDays : "unchanged");
            } else {
                log.info("No parameters provided to update");
                return ResponseEntity.ok("No parameters updated");
            }

            String contextChangedParams = buildContextChangedParams(warningDays, urgentDays);

            OperationHistoryRequest operationHistoryRequestDTO = new OperationHistoryRequest();
            operationHistoryRequestDTO.setActionCode("CONF-001");
            operationHistoryRequestDTO.setContextParameter(contextChangedParams);
            operationHistoryService.createOperationHistory(operationHistoryRequestDTO);

            return ResponseEntity.ok("Parameters updated successfully");
        } catch (Exception e) {
            log.error("Failed to update parameters: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update parameters: " + e.getMessage());
        }
    }

    @GetMapping("/getParams")
    @Operation(
            summary = "Obter parâmetros",
            description = "Endpoint que retorna os parâmetros de aviso/urgência da página principal"
    )
    public ResponseEntity<ParamsResponse> getParams() {
        log.info("Fetching configuration parameters for warning_days_varname={} and urgent_days_varname={}",
                warningDaysVarName, urgentDaysVarName);
        ParamsResponse params = new ParamsResponse();

        configurationService.getConfigurationString(warningDaysVarName)
                .ifPresent(config -> {
                    log.debug("Found {}: {}", warningDaysVarName, config.getVarvalue());
                    params.setWarningDays(config.getVarvalue());
                });
        configurationService.getConfigurationString(urgentDaysVarName)
                .ifPresent(config -> {
                    log.debug("Found {}: {}", urgentDaysVarName, config.getVarvalue());
                    params.setUrgencyDays(config.getVarvalue());
                });

        if (params.getWarningDays() == null) {
            log.warn("No configuration found for {}", warningDaysVarName);
            params.setWarningDays("7"); // Default value
        }
        if (params.getUrgencyDays() == null) {
            log.warn("No configuration found for {}", urgentDaysVarName);
            params.setUrgencyDays("2"); // Default value
        }

        log.info("Returning ParamsDTO: warningDays={}, urgentDays={}",
                params.getWarningDays(), params.getUrgencyDays());
        return ResponseEntity.ok(params);
    }

    public String buildContextChangedParams(Integer warningDays, Integer urgentDays) {
        StringJoiner params = new StringJoiner(" e ");

        if (warningDays != null) {
            params.add("Dias de aviso");
        }
        if (urgentDays != null) {
            params.add("Dias de urgência");
        }

        return params.toString();
    }
}