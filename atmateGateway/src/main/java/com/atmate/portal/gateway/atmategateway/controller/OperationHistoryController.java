package com.atmate.portal.gateway.atmategateway.controller;

import com.atmate.portal.gateway.atmategateway.dto.OperationHistoryResponse;
import com.atmate.portal.gateway.atmategateway.dto.OperationHistoryRequest;
import com.atmate.portal.gateway.atmategateway.database.services.OperationHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
@RestController
@RequestMapping("/operation-history")
@Slf4j
@Tag(name = "Histórico de operações")
public class OperationHistoryController {

    @Autowired
    OperationHistoryService operationHistoryService;

    @GetMapping
    @Operation(
            summary = "Obter histórico de operações",
            description = "Endpoint que retorna o histórico de operações."
    )
    public ResponseEntity<Page<OperationHistoryResponse>> getOperationHistory(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String actionCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.info("Fetching operation history with filters: userId={}, actionCode={}, startDate={}, endDate={}, page={}, size={}, sortBy={}, sortDir={}",
                userId, actionCode, startDate, endDate, page, size, sortBy, sortDir);

        // Converta LocalDate para LocalDateTime se o seu serviço esperar esse tipo
        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(23, 59, 59) : null; // Para incluir o dia todo

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        Page<OperationHistoryResponse> history = operationHistoryService.getOperationHistory(userId, actionCode, startDateTime, endDateTime, pageable);

        return ResponseEntity.ok(history);
    }

    @PostMapping
    @Operation(
            summary = "Registar operação no histórico de operações",
            description = "Endpoint que cria um registo no histórico de operações."
    )
    public ResponseEntity<OperationHistoryResponse> createOperationHistory(@Valid @RequestBody OperationHistoryRequest request) {
        log.info("Creating operation history: userId={}, actionCode={}, contextParameter={}",
                request.getUserId(), request.getActionCode(), request.getContextParameter());

        OperationHistoryResponse createdOperation = operationHistoryService.createOperationHistory(request);
        return ResponseEntity.status(201).body(createdOperation);
    }

}
