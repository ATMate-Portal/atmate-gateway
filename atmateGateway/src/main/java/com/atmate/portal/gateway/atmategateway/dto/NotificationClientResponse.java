package com.atmate.portal.gateway.atmategateway.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationClientResponse {
    private int clientId;
    private String notificationType;
    private String taxType;
    private String status;
    private String title;
    private String message;
    private LocalDateTime sendDate;
}
