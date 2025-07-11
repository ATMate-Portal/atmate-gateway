package com.atmate.portal.gateway.atmategateway.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.Date;

@Data
@Getter
@Builder
public class ErrorResponse {
    private Date timestamp;
    private int status;
    private String error;
    private String message;
    private String errorCode;
    private String path;
}

