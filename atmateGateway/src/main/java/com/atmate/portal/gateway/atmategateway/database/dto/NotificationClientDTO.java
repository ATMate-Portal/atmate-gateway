package com.atmate.portal.gateway.atmategateway.database.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationClientDTO {
    private int clientId;
    private String notificationType;
    private String taxType;
    private String status;
    private String title;
    private String message;
    private LocalDateTime sendDate;
}
