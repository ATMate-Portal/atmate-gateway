package com.atmate.portal.gateway.atmategateway.database.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationHistoryRequestDTO {
    private Integer userId;

    private String actionCode;

    private String contextParameter;
}