package com.atmate.portal.gateway.atmategateway.controller;

import com.atmate.portal.gateway.atmategateway.database.dto.OperationHistoryDTO;
import com.atmate.portal.gateway.atmategateway.database.dto.OperationHistoryRequestDTO;
import com.atmate.portal.gateway.atmategateway.database.dto.UniqueUserDTO;
import com.atmate.portal.gateway.atmategateway.database.services.OperationHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import java.util.List;

@RestController
@RequestMapping("/operation-history")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Histórico de operações")
public class OperationHistoryController {

    @Autowired
    OperationHistoryService operationHistoryService;

    /**
     * Retrieves the operation history with optional filters and pagination.
     *
     * @param userId      Optional user ID filter.
     * @param actionCode  Optional action code filter (e.g., CHECK-001).
     * @param startDate   Optional start date filter (inclusive).
     * @param endDate     Optional end date filter (inclusive).
     * @param page        Page number (0-based, default 0).
     * @param size        Page size (default 20).
     * @param sortBy      Field to sort by (default created_at).
     * @param sortDir     Sort direction (asc or desc, default desc).
     * @return A paginated list of operation history entries.
     */
    @GetMapping
    @Operation(
            summary = "Obter histórico de operações",
            description = "Endpoint que retorna o histórico de operações."
    )
    public ResponseEntity<Page<OperationHistoryDTO>> getOperationHistory(
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
        Page<OperationHistoryDTO> history = operationHistoryService.getOperationHistory(userId, actionCode, startDateTime, endDateTime, pageable);

        return ResponseEntity.ok(history);
    }

    /**
     * Inserts a new operation history entry.
     *
     * @param request The operation history request containing userId, actionCode, and optional contextParameter.
     * @return The created operation history entry.
     */
    @PostMapping
    @Operation(
            summary = "Registar operação no histórico de operações",
            description = "Endpoint que cria um registo no histórico de operações."
    )
    public ResponseEntity<OperationHistoryDTO> createOperationHistory(@Valid @RequestBody OperationHistoryRequestDTO request) {
        log.info("Creating operation history: userId={}, actionCode={}, contextParameter={}",
                request.getUserId(), request.getActionCode(), request.getContextParameter());

        OperationHistoryDTO createdOperation = operationHistoryService.createOperationHistory(request);
        return ResponseEntity.status(201).body(createdOperation);
    }


    /**
     * Retrieves a list of unique users who have operations, optionally filtered by date.
     * This is used to populate the user filter dropdown in the UI.
     *
     * @param startDate Optional start date filter.
     * @param endDate   Optional end date filter.
     * @return A list of unique users (ID and username).
     */
    @GetMapping("/unique-users")
    @Operation(
            summary = "Obter utilizadores únicos com operações",
            description = "Retorna uma lista de utilizadores únicos que têm operações registadas, para popular filtros na UI."
    )
    public ResponseEntity<List<UniqueUserDTO>> getUniqueUsers(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("Fetching unique users with operations between {} and {}", startDate, endDate);


        List<UniqueUserDTO> users = operationHistoryService.getUniqueUsersWithOperations();
        return ResponseEntity.ok(users);
    }

}
