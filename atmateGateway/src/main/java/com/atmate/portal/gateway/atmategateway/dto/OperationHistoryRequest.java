package com.atmate.portal.gateway.atmategateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationHistoryRequest {
    private Integer userId;

    private String actionCode;

    private String contextParameter;
}