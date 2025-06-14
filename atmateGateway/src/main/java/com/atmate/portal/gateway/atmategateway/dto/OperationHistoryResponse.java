package com.atmate.portal.gateway.atmategateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationHistoryResponse {
    private int id;
    private int userId;
    private String username;
    private String userAction;
    private LocalDateTime createdAt;
}
